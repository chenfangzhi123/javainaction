/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yuanfang.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yuanfang.zookeeper.RecoveredAssignments.RecoveryCallback;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * Application master using the curator framework. This code
 * example uses the following curator features:
 * 1- The curator zookeeper client;
 * 2- The fluent API to zookeeper operations;
 * 3- The leader latch primitive;
 * 4- The children cache implementation to hold and manage workers and tasks. *
 *
 * @author chen
 */
@SuppressWarnings("Duplicates")
public class CuratorMasterLatch implements Closeable, LeaderLatchListener {

    private static final Logger LOG = LoggerFactory.getLogger(CuratorMasterLatch.class);
    private final LeaderLatch leaderLatch;
    private final PathChildrenCache workersCache;
    private final PathChildrenCache tasksCache;
    private final String ASSIGN = "/assign";
    private final String TASK = "/tasks";
    CountDownLatch recoveryLatch = new CountDownLatch(0);
    Random rand = new Random(System.currentTimeMillis());
    private String myId;
    private CuratorFramework client;

    /**
     * 使用一个监听器来处理主节点的回调和监听事件。工作者和任务的事件会直接在{@link PathChildrenCacheListener}中进行处理
     */
    CuratorListener masterListener = (client, event) -> {
        try {
            LOG.info("Event path: " + event.getPath());
            switch (event.getType()) {
                case CHILDREN:
                    if (event.getPath().contains(ASSIGN)) {
                        LOG.info("Succesfully got a list of assignments: {} tasks", event.getChildren().size());
                        /* ---------------- 下面这些操作是不是应该保证原子性，不然会导致任务的丢失 -------------- */
                        //删除已经移除的工作者的任务
                        for (String task : event.getChildren()) {
                            deleteAssignment(event.getPath() + "/" + task);
                        }
                        //删除代表这个工作者接受任务的节点
                        deleteAssignment(event.getPath());
                        //重新分配任务
                        assignTasks(event.getChildren());
                        /* ----------------  -------------- */
                    } else {
                        LOG.warn("Unexpected event: " + event.getPath());
                    }
                    break;
                case CREATE:
                    if (event.getPath().contains(ASSIGN)) {
                        // 任务分配成功后删除任务
                        LOG.info("Task assigned correctly: " + event.getName());
                        String number = event.getPath().substring(event.getPath().lastIndexOf('-') + 1);
                        this.client.delete().inBackground().forPath("/tasks/task-" + number);
                        recoveryLatch.countDown();
                    }
                    break;
                case DELETE:
                    /**
                     * 有两种删除节点的情况
                     * 1- 工作者出错时，移除已经分配的任务
                     * 2- 分配了一个任务(/assign下)，删除预分配任务(/tasks)
                     */
                    if (event.getPath().contains(TASK)) {
                        LOG.info("Result of delete operation: " + event.getResultCode() + ", " + event.getPath());
                    } else if (event.getPath().contains(ASSIGN)) {
                        LOG.info("Task correctly deleted: " + event.getPath());
                    }
                    break;
                case WATCHED:
                    // There is no case implemented currently.
                    break;
                default:
                    LOG.error("Default case: " + event.getType());
            }
        } catch (Exception e) {
            LOG.error("Exception while processing event.", e);
            try {
                close();
            } catch (IOException ioe) {
                LOG.error("IOException while closing.", ioe);
            }
        }
    };
    PathChildrenCacheListener workersCacheListener = (client, event) -> {
        if (event.getType() == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
            try {
                //如果工作者移除，则监听该工作者下的任务分配
                this.client.getChildren().inBackground().forPath("/assign/" + event.getData().getPath().replaceFirst("/workers/", ""));
            } catch (Exception e) {
                LOG.error("Exception while trying to re-assign tasks", e);
            }
        }
    };
    PathChildrenCacheListener tasksCacheListener = (client, event) -> {
        if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
            try {
                //如果有任务增加，分配任务
                assignTask(event.getData().getPath().replaceFirst("/tasks/", ""), event.getData().getData());
            } catch (Exception e) {
                LOG.error("Exception when assigning task.", e);
            }
        }
    };
    UnhandledErrorListener errorsListener = (message, e) -> {
        LOG.error("Unrecoverable error: " + message, e);
        try {
            close();
        } catch (IOException ioe) {
            LOG.warn("Exception when closing.", ioe);
        }
    };

    /**
     * 创建一个Curator客户端
     *
     * @param myId        主节点的唯一标志
     * @param hostPort    zookeeper的主机列表
     * @param retryPolicy 失败时的重试策略
     */
    public CuratorMasterLatch(String myId, String hostPort, RetryPolicy retryPolicy) {
        LOG.info(myId + ": " + hostPort);

        this.myId = myId;
        this.client = CuratorFrameworkFactory.newClient(hostPort, retryPolicy);
        this.leaderLatch = new LeaderLatch(this.client, "/master", myId);
        this.workersCache = new PathChildrenCache(this.client, "/workers", true);
        this.tasksCache = new PathChildrenCache(this.client, TASK, true);
    }

    public static void main(String[] args) {
        try {
            CuratorMasterLatch master = new CuratorMasterLatch(args[0], args[1],
                new ExponentialBackoffRetry(1000, 5));
            master.startZK();
            master.bootstrap();
            master.runForMaster();
        } catch (Exception e) {
            LOG.error("Exception while running curator master.", e);
        }
    }

    public void startZK() {
        client.start();
    }

    /**
     * 项目启动时预定义一些节点
     *
     * @throws Exception
     */
    public void bootstrap() throws Exception {
        client.create().forPath("/workers", new byte[0]);
        client.create().forPath(ASSIGN, new byte[0]);
        client.create().forPath(TASK, new byte[0]);
        client.create().forPath("/status", new byte[0]);
    }

    public void runForMaster() throws Exception {
        //注册监听器
        client.getCuratorListenable().addListener(masterListener);
        client.getUnhandledErrorListenable().addListener(errorsListener);
        //主节点选举
        leaderLatch.addListener(this);
        leaderLatch.start();
    }

    /**
     * 等待直到变成主节点
     *
     * @throws InterruptedException
     */
    public void awaitLeadership() throws InterruptedException, EOFException {
        leaderLatch.await();
    }

    /**
     * 成为主节点时执行
     *
     * @throws Exception
     */
    @Override
    public void isLeader() {
        try {
            //监视工作者列表
            workersCache.getListenable().addListener(workersCacheListener);
            workersCache.start();
            RecoveredAssignments recoveredAssignments = new RecoveredAssignments(client.getZookeeperClient().getZooKeeper());
            //执行上次主节点出错后的恢复工作
            recoveredAssignments.recover((rc, tasks) -> {
                try {
                    if (rc == RecoveryCallback.FAILED) {
                        LOG.warn("Recovery of assigned tasks failed.");
                    } else {
                        LOG.info("Assigning recovered tasks");
                        recoveryLatch = new CountDownLatch(tasks.size());
                        // 这里的逻辑可以不执行，下面反正也会监视任务，走的是同一套逻辑，
                        // recoveredAssignments只需要把分配给失效从节点的任务重新挂到/tasks下即可
                        assignTasks(tasks);
                    }
                    new Thread(() -> {
                        try {
                            // 等待直到恢复完成
                            recoveryLatch.await();
                            //开始监视任务
                            tasksCache.getListenable().addListener(tasksCacheListener);
                            tasksCache.start();
                        } catch (Exception e) {
                            LOG.warn("Exception while assigning and getting tasks.", e);
                        }
                    }).start();
                } catch (Exception e) {
                    LOG.error("Exception while executing the recovery callback", e);
                }
            });
        } catch (Exception e) {
            LOG.error("Exception when starting leadership", e);
        }
    }

    @Override
    public void notLeader() {
        LOG.info("Lost leadership");
        try {
            close();
        } catch (IOException e) {
            LOG.warn("Exception while closing", e);
        }
    }

    @Override
    public void close() throws IOException {
        LOG.info("Closing");
        leaderLatch.close();
        client.close();
    }

    void assignTasks(List<String> tasks) throws Exception {
        for (String task : tasks) {
            assignTask(task, client.getData().forPath("/tasks/" + task));
        }
    }

    void assignTask(String task, byte[] data) throws Exception {
        //随机选择一个工作者
        List<ChildData> workersList = workersCache.getCurrentData();
        LOG.info("Assigning task {}, data {}", task, new String(data));
        String designatedWorker = workersList.get(rand.nextInt(workersList.size())).getPath().replaceFirst("/workers/", "");
        //分配任务给随机工作者
        String path = "/assign/" + designatedWorker + "/" + task;
        //默认ACL为OPEN_ACL_UNSAFE
        client.create().withMode(CreateMode.PERSISTENT).inBackground().forPath(path, data);
    }

    void deleteAssignment(String path) throws Exception {
        //删除任务分配
        LOG.info("Deleting assignment: {}", path);
        client.delete().inBackground().forPath(path);
    }
}
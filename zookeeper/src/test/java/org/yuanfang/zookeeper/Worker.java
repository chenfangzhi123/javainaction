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

import org.apache.zookeeper.AsyncCallback.*;
import org.apache.zookeeper.*;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Worker implements Watcher, Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(Worker.class);
    protected ChildrenCache assignedTasksCache = new ChildrenCache();
    String name;
    String status;
    VoidCallback taskVoidCallback = new VoidCallback() {
        public void processResult(int rc, String path, Object rtx) {
            switch (Code.get(rc)) {
                case CONNECTIONLOSS:
                    break;
                case OK:
                    LOG.info("Task correctly deleted: " + path);
                    break;
                default:
                    LOG.error("Failed to delete task data" + KeeperException.create(Code.get(rc), path));
            }
        }
    };
    private ZooKeeper zk;
    StatCallback statusUpdateCallback = new StatCallback() {
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            switch (Code.get(rc)) {
                case CONNECTIONLOSS:
                    updateStatus((String) ctx);
                    return;
            }
        }
    };
    StringCallback taskStatusCreateCallback = new StringCallback() {
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (Code.get(rc)) {
                case CONNECTIONLOSS:
                    zk.create(path + "/status", "done".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT,
                        taskStatusCreateCallback, null);
                    break;
                case OK:
                    LOG.info("Created status znode correctly: " + name);
                    break;
                case NODEEXISTS:
                    LOG.warn("Node exists: " + path);
                    break;
                default:
                    LOG.error("Failed to create task data: ", KeeperException.create(Code.get(rc), path));
            }
        }
    };
    private String hostPort;
    private String serverId = Integer.toHexString((new Random()).nextInt());
    StringCallback createAssignCallback = new StringCallback() {
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (Code.get(rc)) {
                case CONNECTIONLOSS:
                    /*
                     * Try again. Note that registering again is not a problem.
                     * If the znode has already been created, then we get a
                     * NODEEXISTS event back.
                     */
                    createAssignNode();
                    break;
                case OK:
                    LOG.info("Assign node created");
                    break;
                case NODEEXISTS:
                    LOG.warn("Assign node already registered");
                    break;
                default:
                    LOG.error("Something went wrong: " + KeeperException.create(Code.get(rc), path));
            }
        }
    };
    StringCallback createWorkerCallback = new StringCallback() {
        public void processResult(int rc, String path, Object ctx, String name) {
            switch (Code.get(rc)) {
                case CONNECTIONLOSS:
                    /*
                     * Try again. Note that registering again is not a problem.
                     * If the znode has already been created, then we get a
                     * NODEEXISTS event back.
                     */
                    register();

                    break;
                case OK:
                    LOG.info("Registered successfully: " + serverId);

                    break;
                case NODEEXISTS:
                    LOG.warn("Already registered: " + serverId);

                    break;
                default:
                    LOG.error("Something went wrong: ",
                        KeeperException.create(Code.get(rc), path));
            }
        }
    };
    private volatile boolean connected = false;
    private volatile boolean expired = false;
    /*
     * In general, it is not a good idea to block the callback thread
     * of the ZooKeeper client. We use a thread pool executor to detach
     * the computation from the callback.
     */
    private ThreadPoolExecutor executor;
    DataCallback taskDataCallback = new DataCallback() {
        public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
            switch (Code.get(rc)) {
                case CONNECTIONLOSS:
                    zk.getData(path, false, taskDataCallback, null);
                    break;
                case OK:
                    /*
                     *  Executing a task in this example is simply printing out
                     *  some string representing the task.
                     */
                    executor.execute(new Runnable() {
                        byte[] data;
                        Object ctx;

                        /*
                         * Initializes the variables this anonymous class needs
                         */
                        public Runnable init(byte[] data, Object ctx) {
                            this.data = data;
                            this.ctx = ctx;

                            return this;
                        }

                        public void run() {
                            LOG.info("Executing your task: " + new String(data));
                            zk.create("/status/" + (String) ctx, "done".getBytes(), Ids.OPEN_ACL_UNSAFE,
                                CreateMode.PERSISTENT, taskStatusCreateCallback, null);
                            zk.delete("/assign/worker-" + serverId + "/" + (String) ctx,
                                -1, taskVoidCallback, null);
                        }
                    }.init(data, ctx));

                    break;
                default:
                    LOG.error("Failed to get task data: ", KeeperException.create(Code.get(rc), path));
            }
        }
    };
    ChildrenCallback tasksGetChildrenCallback = new ChildrenCallback() {
        public void processResult(int rc, String path, Object ctx, List<String> children) {
            switch (Code.get(rc)) {
                case CONNECTIONLOSS:
                    getTasks();
                    break;
                case OK:
                    if (children != null) {
                        executor.execute(new Runnable() {
                            List<String> children;
                            DataCallback cb;

                            /*
                             * Initializes input of anonymous class
                             */
                            public Runnable init(List<String> children, DataCallback cb) {
                                this.children = children;
                                this.cb = cb;

                                return this;
                            }

                            public void run() {
                                if (children == null) {
                                    return;
                                }

                                LOG.info("Looping into tasks");
                                setStatus("Working");
                                for (String task : children) {
                                    LOG.trace("New task: {}", task);
                                    zk.getData("/assign/worker-" + serverId + "/" + task,
                                        false,
                                        cb,
                                        task);
                                }
                            }
                        }.init(assignedTasksCache.addedAndSet(children), taskDataCallback));
                    }
                    break;
                default:
                    System.out.println("getChildren failed: " + KeeperException.create(Code.get(rc), path));
            }
        }
    };
    Watcher newTaskWatcher = new Watcher() {
        public void process(WatchedEvent e) {
            if (e.getType() == EventType.NodeChildrenChanged) {
                assert new String("/assign/worker-" + serverId).equals(e.getPath());

                getTasks();
            }
        }
    };
    private int executionCount;

    /**
     * Creates a new Worker instance.
     *
     * @param hostPort
     */
    public Worker(String hostPort) {
        this.hostPort = hostPort;
        this.executor = new ThreadPoolExecutor(1, 1,
            1000L,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(200));
    }

    /**
     * Main method showing the steps to execute a worker.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        Worker w = new Worker(args[0]);
        w.startZK();

        while (!w.isConnected()) {
            Thread.sleep(100);
        }
        /*
         * bootstrap() create some necessary znodes.
         */
        w.bootstrap();

        /*
         * Registers this worker so that the leader knows that
         * it is here.
         */
        w.register();

        /*
         * Getting assigned tasks.
         */
        w.getTasks();

        while (!w.isExpired()) {
            Thread.sleep(1000);
        }
    }

    /**
     * Creates a ZooKeeper session.
     *
     * @throws IOException
     */
    public void startZK() throws IOException {
        zk = new ZooKeeper(hostPort, 15000, this);
    }

    /**
     * Checks if this client is connected.
     *
     * @return boolean
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Bootstrapping here is just creating a /assign parent
     * znode to hold the tasks assigned to this worker.
     */
    public void bootstrap() {
        createAssignNode();
    }
    /*
     ***************************************
     ***************************************
     * Methods to wait for new assignments.*
     ***************************************
     ***************************************
     */

    /**
     * Registering the new worker, which consists of adding a worker
     * znode to /workers.
     */
    public void register() {
        name = "worker-" + serverId;
        zk.create("/workers/" + name,
            "Idle".getBytes(),
            Ids.OPEN_ACL_UNSAFE,
            CreateMode.EPHEMERAL,
            createWorkerCallback, null);
    }

    void getTasks() {
        zk.getChildren("/assign/worker-" + serverId,
            newTaskWatcher,
            tasksGetChildrenCallback,
            null);
    }

    /**
     * Checks if ZooKeeper session is expired.
     *
     * @return
     */
    public boolean isExpired() {
        return expired;
    }

    void createAssignNode() {
        zk.create("/assign/worker-" + serverId, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT,
            createAssignCallback, null);
    }

    /**
     * Deals with session events like connecting
     * and disconnecting.
     *
     * @param e new event generated
     */
    public void process(WatchedEvent e) {
        LOG.info(e.toString() + ", " + hostPort);
        if (e.getType() == Event.EventType.None) {
            switch (e.getState()) {
                case SyncConnected:
                    /*
                     * Registered with ZooKeeper
                     */
                    connected = true;
                    break;
                case Disconnected:
                    connected = false;
                    break;
                case Expired:
                    expired = true;
                    connected = false;
                    LOG.error("Session expired");
                default:
                    break;
            }
        }
    }

    synchronized void changeExecutionCount(int countChange) {
        executionCount += countChange;
        if (executionCount == 0 && countChange < 0) {
            // we have just become idle
            setStatus("Idle");
        }
        if (executionCount == 1 && countChange > 0) {
            // we have just become idle
            setStatus("Working");
        }
    }

    public void setStatus(String status) {
        this.status = status;
        updateStatus(status);
    }

    synchronized private void updateStatus(String status) {
        if (status == this.status) {
            zk.setData("/workers/" + name, status.getBytes(), -1,
                statusUpdateCallback, status);
        }
    }

    /**
     * Closes the ZooKeeper session.
     */
    @Override
    public void close()
        throws IOException {
        LOG.info("Closing");
        try {
            zk.close();
        } catch (InterruptedException e) {
            LOG.warn("ZooKeeper interrupted while closing");
        }
    }
}

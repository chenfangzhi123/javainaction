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

import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.AsyncCallback.DataCallback;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.AsyncCallback.VoidCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个类实现了在主崩溃之后恢复作业的逻辑.
 * 主要措施是确定已经分配的任务并分配那些没有分配的任务
 *
 * @author chen
 */
@SuppressWarnings("Duplicates")
public class RecoveredAssignments {

    private static final Logger LOG = LoggerFactory.getLogger(RecoveredAssignments.class);
    /**
     * 需要恢复的任务
     */
    List<String> tasks;
    /**
     * 正在执行的任务
     */
    List<String> assignments;
    /**
     * 已经完成的任务
     */
    List<String> status;
    /**
     * 存活的工作者
     */
    List<String> activeWorkers;
    /**
     * 分配了任务的工作者
     */
    List<String> assignedWorkers;

    RecoveryCallback cb;
    ZooKeeper zk;

    VoidCallback taskDeletionCallback = (rc, path, rtx) -> {
        switch (Code.get(rc)) {
            case CONNECTIONLOSS:
                deleteAssignment(path);
                break;
            case OK:
                LOG.info("Task correctly deleted: " + path);
                break;
            default:
                LOG.error("Failed to delete task data" +
                    KeeperException.create(Code.get(rc), path));
        }
    };
    /**
     * Recreate znode callback
     */
    StringCallback recreateTaskCallback = (rc, path, ctx, name) -> {
        switch (Code.get(rc)) {
            case CONNECTIONLOSS:
                recreateTask((RecreateTaskCtx) ctx);
                break;
            case OK:
                deleteAssignment(((RecreateTaskCtx) ctx).path);

                break;
            case NODEEXISTS:
                LOG.warn("Node shouldn't exist: " + path);

                break;
            default:
                LOG.error("Something wwnt wrong when recreating task",
                    KeeperException.create(Code.get(rc)));
        }
    };
    /**
     * Get task data reassign callback.
     */
    DataCallback getDataReassignCallback = (rc, path, ctx, data, stat) -> {
        switch (Code.get(rc)) {
            case CONNECTIONLOSS:
                getDataReassign(path, (String) ctx);
                break;
            case OK:
                recreateTask(new RecreateTaskCtx(path, (String) ctx, data));
                break;
            default:
                LOG.error("Something went wrong when getting data ",
                    KeeperException.create(Code.get(rc)));
        }
    };
    ChildrenCallback statusCallback = new ChildrenCallback() {
        @Override
        public void processResult(int rc,
                                  String path,
                                  Object ctx,
                                  List<String> children) {
            switch (Code.get(rc)) {
                case CONNECTIONLOSS:
                    getStatuses();
                    break;
                case OK:
                    LOG.info("Processing assignments for recovery");
                    status = children;
                    processAssignments();
                    break;
                default:
                    LOG.error("getChildren failed", KeeperException.create(Code.get(rc), path));
                    cb.recoveryComplete(RecoveryCallback.FAILED, null);
            }
        }
    };
    ChildrenCallback workerAssignmentsCallback = new ChildrenCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, List<String> children) {
            switch (Code.get(rc)) {
                case CONNECTIONLOSS:
                    getWorkerAssignments(path);
                    break;
                case OK:
                    String worker = path.replace("/assign/", "");
                    //对于已经分配的任务，如果对应的工作者还存活，则直接添加到已分配列表。
                    //否则需要重新分配
                    if (activeWorkers.contains(worker)) {
                        assignments.addAll(children);
                    } else {
                        for (String task : children) {
                            if (!tasks.contains(task)) {
                                tasks.add(task);
                                getDataReassign(path, task);
                                // todoBychenfangzhi ----2018/10/8 14:17------>这里原来是放在else中，应该是写错了
                                deleteAssignment(path + "/" + task);
                            }
                        }
                        // todoBychenfangzhi ----2018/10/8 14:10------>这里原来应该是有错误，应该是删除所有已分配在删除父节点
                        deleteAssignment(path);
                    }
                    assignedWorkers.remove(worker);
                    //如果我们已分配工作者已经为空，开始检查状态
                    if (assignedWorkers.size() == 0) {
                        LOG.info("Getting statuses for recovery");
                        getStatuses();
                    }
                    break;
                case NONODE:
                    LOG.info("No such znode exists: " + path);
                    break;
                default:
                    LOG.error("getChildren failed", KeeperException.create(Code.get(rc), path));
                    cb.recoveryComplete(RecoveryCallback.FAILED, null);
            }
        }
    };
    ChildrenCallback workersCallback = new ChildrenCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, List<String> children) {
            switch (Code.get(rc)) {
                case CONNECTIONLOSS:
                    getWorkers(ctx);
                    break;
                case OK:
                    LOG.info("Getting worker assignments for recovery: " + children.size());

                    /*
                     * No worker available yet, so the master is probably let's just return an empty list.
                     */
                    if (children.size() == 0) {
                        LOG.warn("Empty list of workers, possibly just starting");
                        cb.recoveryComplete(RecoveryCallback.OK, new ArrayList<>());
                        break;
                    }
                    //还存活的工作节点
                    activeWorkers = children;

                    for (String s : assignedWorkers) {
                        getWorkerAssignments("/assign/" + s);
                    }

                    break;
                default:
                    LOG.error("getChildren failed", KeeperException.create(Code.get(rc), path));
                    cb.recoveryComplete(RecoveryCallback.FAILED, null);
            }
        }
    };
    ChildrenCallback assignedWorkersCallback = new ChildrenCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, List<String> children) {
            switch (Code.get(rc)) {
                case CONNECTIONLOSS:
                    getAssignedWorkers();
                    break;
                case OK:
                    assignedWorkers = children;
                    getWorkers(children);
                    break;
                default:
                    LOG.error("getChildren failed", KeeperException.create(Code.get(rc), path));
                    cb.recoveryComplete(RecoveryCallback.FAILED, null);
            }
        }
    };
    ChildrenCallback tasksCallback = new ChildrenCallback() {
        @Override
        public void processResult(int rc, String path, Object ctx, List<String> children) {
            switch (Code.get(rc)) {
                case CONNECTIONLOSS:
                    getTasks();
                    break;
                case OK:
                    LOG.info("Getting tasks for recovery");
                    tasks = children;
                    getAssignedWorkers();
                    break;
                default:
                    LOG.error("getChildren failed", KeeperException.create(Code.get(rc), path));
                    cb.recoveryComplete(RecoveryCallback.FAILED, null);
            }
        }
    };

    /**
     * Recover unassigned tasks.
     *
     * @param zk
     */
    public RecoveredAssignments(ZooKeeper zk) {
        this.zk = zk;
        this.assignments = new ArrayList<>();
    }

    /**
     * 开始执行恢复逻辑
     *
     * @param recoveryCallback
     */
    public void recover(RecoveryCallback recoveryCallback) {
        cb = recoveryCallback;
        getTasks();
    }

    private void getTasks() {
        zk.getChildren("/tasks", false, tasksCallback, null);
    }

    private void getAssignedWorkers() {
        zk.getChildren("/assign", false, assignedWorkersCallback, null);
    }

    private void getWorkers(Object ctx) {
        zk.getChildren("/workers", false, workersCallback, ctx);
    }

    private void getWorkerAssignments(String s) {
        zk.getChildren(s, false, workerAssignmentsCallback, null);
    }

    /**
     * 获取需要重新分配的节点
     *
     * @param path
     * @param task
     */
    void getDataReassign(String path, String task) {
        zk.getData(path, false, getDataReassignCallback, task);
    }

    /**
     * 重新创建任务节点
     *
     * @param ctx Recreate text context
     */
    void recreateTask(RecreateTaskCtx ctx) {
        zk.create("/tasks/" + ctx.task, ctx.data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, recreateTaskCallback,
            ctx);
    }

    void getStatuses() {
        zk.getChildren("/status", false, statusCallback, null);
    }

    private void processAssignments() {
        // 剔除正在执行的任务
        LOG.info("Size of tasks: " + tasks.size());
        for (String s : assignments) {
            LOG.info("Assignment: " + s);
            deleteAssignment("/tasks/" + s);
            tasks.remove(s);
        }

        //剔除已经完成的任务
        LOG.info("Size of tasks after assignment filtering: " + tasks.size());
        for (String s : status) {
            LOG.info("Checking task: {} ", s);
            deleteAssignment("/tasks/" + s);
            tasks.remove(s);
        }

        // 剩余的都是要继续执行的任务
        LOG.info("Size of tasks after status filtering: " + tasks.size());
        cb.recoveryComplete(RecoveryCallback.OK, tasks);
    }

    /**
     * Delete assignment of absent worker
     *
     * @param path Path of znode to be deleted
     */
    void deleteAssignment(String path) {
        zk.delete(path, -1, taskDeletionCallback, null);
    }

    public interface RecoveryCallback {

        int OK = 0;
        int FAILED = -1;

        /**
         * 恢复失败或者成功时都会回调
         *
         * @param rc
         * @param tasks
         */
        void recoveryComplete(int rc, List<String> tasks);
    }

    /**
     * 恢复任务的上下文
     */
    class RecreateTaskCtx {

        String path;
        String task;
        byte[] data;

        RecreateTaskCtx(String path, String task, byte[] data) {
            this.path = path;
            this.task = task;
            this.data = data;
        }
    }
}

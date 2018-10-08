package org.yuanfang.zookeeper;

import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yuanfang.zookeeper.Client.TaskObject;

public class TestCuratorMaster extends BaseTestCase {

    private static final Logger LOG = LoggerFactory.getLogger(TestCuratorMaster.class);
    private final String HOST = "localhost:";


    @Test(timeout = 60000)
    public void testTaskAssignmentLatch() throws Exception {
        LOG.info("Starting master (taskAssignment)");
        CuratorMasterLatch m = new CuratorMasterLatch("M1", HOST + port,
            new ExponentialBackoffRetry(1000, 5));
        m.startZK();
        m.bootstrap();
        m.runForMaster();
        LOG.info("Going to wait for leadership");
        m.awaitLeadership();

        LOG.info("Starting worker");
        Worker w1 = new Worker(HOST + port);
        Worker w2 = new Worker(HOST + port);
        Worker w3 = new Worker(HOST + port);

        w1.startZK();
        w2.startZK();
        w3.startZK();

        while (!w1.isConnected() && !w2.isConnected() && !w3.isConnected()) {
            Thread.sleep(100);
        }

        /*
         * bootstrap() create some necessary znodes.
         */
        w1.bootstrap();
        w2.bootstrap();
        w3.bootstrap();

        /*
         * Registers this worker so that the leader knows that
         * it is here.
         */
        w1.register();
        w2.register();
        w3.register();

        w1.getTasks();
        w2.getTasks();
        w3.getTasks();

        LOG.info("Starting client");
        Client c = new Client(HOST + port);
        c.startZK();

        while (!c.isConnected() &&
            !w1.isConnected() &&
            !w2.isConnected() &&
            !w3.isConnected()) {
            Thread.sleep(100);
        }

        TaskObject task = null;
        for (int i = 1; i < 200; i++) {
            task = new TaskObject();
            c.submitTask("Sample task taskAssignment " + i, task);
            task.waitUntilDone();
            Assert.assertTrue("Task not done", task.isDone());
        }

        w1.close();
        w2.close();
        w3.close();
        m.close();
    }
}

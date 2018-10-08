package org.yuanfang.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/10/8-20:08
 * @ModifiedBy:
 */

public class PathChildrenCacheListenerTest {

    /**
     * 开启时原来有的所有子节点都会触发一次
     */
    public static final PathChildrenCacheListener tasksCacheListener = (client, event) -> {
        if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
            System.out.println("receive:" + event.getData().getPath());
        }
    };
    private static final String HOST_PORT = "192.168.199.128:2181";

    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(HOST_PORT, new ExponentialBackoffRetry(1000, 5));
        client.start();
        final PathChildrenCache pathChildrenCache = new PathChildrenCache(client, "/test", true);
        pathChildrenCache.getListenable().addListener(tasksCacheListener);
        pathChildrenCache.start();
        System.in.read();
    }
}

package org.yuanfang.juc.executors;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/10/24-22:16
 * @ModifiedBy:
 */
public class ListTest {
    public static void main(String[] args) {
        List<Integer> integers = new ArrayList<>();
        integers.add(0, 1);
        integers.add(0, 1);
        integers.add(0, 1);
        System.out.println(integers);
    }
}

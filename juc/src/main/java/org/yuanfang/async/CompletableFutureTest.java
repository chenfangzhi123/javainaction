package org.yuanfang.async;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/11/7-23:22
 * @ModifiedBy:
 */
@Slf4j
public class CompletableFutureTest {
    public static void main(String[] args) throws IOException {
        // CompletableFuture.runAsync(() -> System.out.println("hello word")).whenComplete((aVoid, throwable) -> System.out.println("任务完成"));
        // long l = System.currentTimeMillis();
        // Integer join = CompletableFuture.supplyAsync(() -> {
        //     try {
        //         Thread.sleep(3000);
        //     } catch (InterruptedException e) {
        //         e.printStackTrace();
        //     }
        //     return 1;
        // }).thenCombine(CompletableFuture.supplyAsync(() -> {
        //     try {
        //         Thread.sleep(3000);
        //     } catch (InterruptedException e) {
        //         e.printStackTrace();
        //     }
        //     return 2;
        // }), Integer::sum).join();
        // System.out.println(System.currentTimeMillis() - l);
        // CompletableFuture.supplyAsync(() -> 12).thenApply(Function.identity()).thenAccept(System.out::println);

        CompletableFuture.supplyAsync(() -> 1).whenComplete((o, throwable) -> {
            System.out.println("when1");
            throw new RuntimeException();
        }).thenAccept(o -> {
            System.out.println("accetp2");
        }).thenApply(aVoid -> {
            System.out.println("apply");
            return 1;
        }).whenComplete((integer, throwable) -> {
            System.out.println("when2");
        }).thenAccept(integer -> {
            System.out.println("accetp");
        });


        System.in.read();
    }
}

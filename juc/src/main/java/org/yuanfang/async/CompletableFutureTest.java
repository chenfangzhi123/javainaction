package org.yuanfang.async;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        // CompletableFuture.supplyAsync(() -> 1).whenComplete((o, throwable) -> {
        //     System.out.println("when1");
        //     throw new RuntimeException();
        // }).thenApply((o) -> {
        //     System.out.println("accetp2");
        //     return o;
        // }).thenApply(aVoid -> {
        //     System.out.println("apply");
        //     return 1;
        // }).whenComplete((integer, throwable) -> {
        //     System.out.println("when2");
        // }).thenAccept(integer -> {
        //     System.out.println("accetp");
        // });
        //
        //
        // CompletableFuture.supplyAsync(() -> {
        //     System.out.println("1");
        //     // throw new RuntimeException();
        //     return 1;
        // }).thenCombine(CompletableFuture.supplyAsync(() -> {
        //     System.out.println("2");
        //     return 2;
        //
        // }), (o, integer) -> {
        //     return 1;
        // });


        CompletableFuture.supplyAsync(() -> {
            System.out.println(1);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            throw new RuntimeException();
        }).runAfterEither(CompletableFuture.supplyAsync(() -> {
            System.out.println(2);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return 1;
        }), () -> System.out.println(123));


        long l = System.currentTimeMillis();


        CompletableFuture[] completableFutures = Arrays.stream(new Integer[]{1, 2, 3})
            .map(operand -> CompletableFuture.supplyAsync(() -> {
                try {
                    System.out.println("执行任务:" + operand);
                    Thread.sleep(1000);
                    return operand;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return 0;
                }
            })).map(integerCompletableFuture -> integerCompletableFuture.thenApply(integer -> {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
                return integer;
            })).toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(completableFutures).join();


        // ExecutorService executorService = Executors.newWorkStealingPool();
        // executorService.execute(() -> {
        //     CompletableFuture.supplyAsync(() -> {
        //         System.out.println(1);
        //         try {
        //             Thread.sleep(2000);
        //         } catch (InterruptedException e) {
        //             e.printStackTrace();
        //         }
        //         throw new RuntimeException();
        //     }).runAfterEither(CompletableFuture.supplyAsync(() -> {
        //         System.out.println(2);
        //         try {
        //             Thread.sleep(1000);
        //         } catch (InterruptedException e) {
        //             e.printStackTrace();
        //         }
        //         return 1;
        //     }), () -> System.out.println(123));
        // });


        System.out.println(System.currentTimeMillis() - l);
        System.in.read();
    }
}

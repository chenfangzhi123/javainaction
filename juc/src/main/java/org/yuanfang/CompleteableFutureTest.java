package org.yuanfang;

import java.util.concurrent.CompletableFuture;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/6/26-14:21
 * @ModifiedBy:
 */
public class CompleteableFutureTest {

    public void exceptionally() {
        String result = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //如果没有抛出异常不会走下面的逻辑
            // if (1 == 1) {
            //     throw new RuntimeException("测试一下异常情况");
            // }
            return "s1";
        }).exceptionally(e -> {
            System.out.println(e.getMessage());
            return "hello world";
        }).join();
        System.out.println(result);
    }

    /**
     * 关于thenApply和thenCompose方法的区别，jdk9文档中已经说明大概就是Stream.map和Stream.flatMap的区别
     * 所以大部分情况下使用thenApply方法
     * 我个人理解见代码1和代码2的注释，
     * <p>
     * 输出可能是多种形式如下面(注意I'm completed输出的位置)：
     * 情况一：
     * run1: 11
     * thenCompose1: 11
     * I'm completed
     * run2: 11
     * RunAsync1: 12
     * Finished
     * 情况二
     * run1: 13
     * thenCompose1: 13
     * run2: 14
     * RunAsync1: 14
     * I'm completed
     * ********************重点**********************
     * 还有不带Asych的方法会改变CompletionStage的完成状态，可以通过运行下面的代码单步调试
     * 在复杂的顺序依赖关系中可以继续深究这个问题
     */
    public void supplyAndcompose2() {
        CompletableFuture<Void> c = CompletableFuture.runAsync(
            //断点5
            () -> System.out.println("run1: " + Thread.currentThread().getId()
            ));
        //断点6
        c.whenComplete((r, t) ->
            //断点1
            System.out.println("I'm completed")
        );

        c.thenCompose(o -> {
            System.out.println("thenCompose1: " + Thread.currentThread().getId());
            return CompletableFuture.runAsync(
                //断点2
                () -> System.out.println("run2: " + Thread.currentThread().getId()
                ));
        }).thenRunAsync(
            //断点3
            () -> System.out.println("RunAsync1: " + Thread.currentThread().getId()
            ));
        //代码1
        //下面写法等价于thenApply
        /*c.thenCompose(o -> {
            System.out.println(1);
            System.out.println(2);
            return CompletableFuture.completedFuture(null);
        });*/
        //代码2
        //下面写法等价于thenApplyAsync
        /*c.thenCompose(o -> {
            return CompletableFuture.supplyAsync(() -> {
                System.out.println(1);
                System.out.println(2);
                return 1;
            });
        });*/


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Finished");
    }

    public void supplyAndcompose() {
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " Printing hello");
            return "abc";
        }).thenCompose((String s) ->
            CompletableFuture.supplyAsync(() -> {
                System.out.println(Thread.currentThread().getName() + " Adding abc");
                return s + " Hello";
            })
        ).thenApplyAsync((String s) -> {
            System.out.println(Thread.currentThread().getName() + " Adding world");
            return s + " World";
        }).thenApplyAsync((String s) -> {
            System.out.println(Thread.currentThread().getName() + " Adding name");
            return s + " player!";
        }).handle((String s, Throwable t) -> {
            System.out.println(s != null ? s : "BLANK");
            System.out.println(t != null ? t.getMessage() : "BLANK Exception");
            return s != null ? s : t.getMessage();
        });
    }

    /**
     * whenComplete方法接收的是Comsume对象，不会对返回结果造成影响
     */
    public void whenComplete() {
        //前一个CompleteableFuture没抛出whenComplete抛出异常时会导致最后抛出异常
        try {
            System.out.println(CompletableFuture.supplyAsync(() -> "123").whenComplete((s, throwable) -> {
                int a = 0 / 0;
            }).join());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //前一个CompleteableFuture抛出异常whenComplete抛出异常时最后只会抛出一开始的异常
        try {
            System.out.println(CompletableFuture.supplyAsync(() -> {
                throw new RuntimeException();
            }).whenComplete((s, throwable) -> {
                int a = 0 / 0;
            }).join());

        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }
}

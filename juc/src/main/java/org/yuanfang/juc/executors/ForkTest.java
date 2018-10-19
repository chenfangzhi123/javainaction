package org.yuanfang.juc.executors;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * @Author: chenfangzhi
 * @Description:
 * @Date: 2018/10/19-12:03
 * @ModifiedBy:
 */
public class ForkTest {
	static ForkJoinPool forkJoinPool = new ForkJoinPool(4);

	public static void main(String[] args) throws IOException {
		// forkJoinPool.execute( () -> {
		// 	System.out.println(Thread.currentThread());
		// });
		// CompletableFuture.runAsync(() -> {
		// 	System.out.println(Thread.currentThread());
		// });

		List<Integer> list = Arrays.asList(1, 2, 3, 45, 6, 657);
		list.parallelStream().map(integer -> {
			System.out.println(Thread.currentThread());
			return integer;
		}).collect(Collectors.toList());

		forkJoinPool.submit(
			() -> {
				list.parallelStream().map(integer -> {
					System.out.println(Thread.currentThread());
					return integer;
				}).collect(Collectors.toList());
			}
		);


		System.in.read();

	}
}

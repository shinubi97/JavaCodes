package com.shinubi.week04;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.LockSupport;

/**
 * @author shinubi
 * Date    2021/12/1
 * @version 1.0.0
 * @Description 主线程等待子线程
 */
public class MainThreadWaitSubThreadDemo {

    public static void main(String[] args) {

        try {
//            method1();
//            method2();
//            method3();
//            method4();
//            method5();
            method6();
//            method7();
//            method8();
//            method9();
//            method10();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 然后退出main线程
    }

    /**
     * 方法 1-->while
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static void method1() throws ExecutionException, InterruptedException {
        FutureTask<Integer> futureTask = new FutureTask<>(new MyCallable());
        int result = 0;
        Thread t = new Thread(futureTask);

        long start = System.currentTimeMillis();
        t.start();
        do {
            result = futureTask.get();
        } while (!futureTask.isDone());
        // 确保  拿到result 并输出
        System.out.print("method1--> while <--");
        System.out.print("异步计算结果为：" + result);
        System.out.println(" 使用时间：" + (System.currentTimeMillis() - start) + " ms");

    }

    /**
     * 方法 2-->synchronized
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static void method2() throws ExecutionException, InterruptedException {

        FutureTask<Integer> futureTask = new FutureTask<>(new MyCallable());
        int result = 0;
        Thread t = new Thread(futureTask);

        long start = System.currentTimeMillis();
        t.start();
        synchronized (MainThreadWaitSubThreadDemo.class) {
            //只有当获取到执行结果了，才会解锁，主线程才会继续运行
            result = futureTask.get();
        }
        // 确保  拿到result 并输出
        System.out.print("method2--> synchronized <--");
        System.out.print("异步计算结果为：" + result);
        System.out.println(" 使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }


    /**
     * 方法3 -->join
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static void method3() throws ExecutionException, InterruptedException {

        FutureTask<Integer> futureTask = new FutureTask<>(new MyCallable());
        Thread t = new Thread(futureTask);

        long start = System.currentTimeMillis();
        t.start();
        t.join();
        int result = futureTask.get();
        // 确保  拿到result 并输出
        System.out.print("method3--> synchronized <--");
        System.out.print("异步计算结果为：" + result);
        System.out.println(" 使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }


    /**
     * 方法4 -->wait/notify
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private static void method4() throws InterruptedException, ExecutionException {

        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            int result = sum();
            synchronized (MainThreadWaitSubThreadDemo.class) {
                MainThreadWaitSubThreadDemo.class.notifyAll();
            }
            return result;
        });
        Thread t = new Thread(futureTask);

        long start = System.currentTimeMillis();
        t.start();
        //调用wait()方法时需要获取该对象的锁
        //The current thread must own this object's monitor.
        //IllegalMonitorStateException - if the current thread is not the owner of the object's monitor.
        synchronized (MainThreadWaitSubThreadDemo.class) {
            MainThreadWaitSubThreadDemo.class.wait();
        }
        int result = futureTask.get();
        // 确保  拿到result 并输出
        System.out.print("method4--> wait/notify <--");
        System.out.print("异步计算结果为：" + result);
        System.out.println(" 使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }

    /**
     * 方法5 -->线程池
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static void method5() throws ExecutionException, InterruptedException {

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        long start = System.currentTimeMillis();
        Future<Integer> future = executorService.submit(new MyCallable());
        //todo 这里为什么会报空指针异常？
//        Future future = executorService.submit(new MyRunnable());
        int result = future.get();
        // 确保  拿到result 并输出
        System.out.print("method5--> ThreadPool <--");
        System.out.print("异步计算结果为：" + result);
        System.out.println(" 使用时间：" + (System.currentTimeMillis() - start) + " ms");
        executorService.shutdown();
    }

    /**
     * 方法6 --> semaphore 信号量
     * TODO 以信号量实现的方式有待商榷
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static void method6() throws ExecutionException, InterruptedException {

        //当permits=1时效果等同于synchronized
        Semaphore semaphore = new Semaphore(1);
        FutureTask<Integer> futureTask = new FutureTask<>(new MyCallable(semaphore, null, null, 1));
        Thread t = new Thread(futureTask);

        long start = System.currentTimeMillis();
        t.start();
        semaphore.acquire();
//        int result = futureTask.get();
        int result = futureTask.get();
        // 确保  拿到result 并输出
        semaphore.release();
        System.out.print("method6--> semaphore <--");
        System.out.print("异步计算结果为：" + result);
        System.out.println(" 使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }

    /**
     * 方法7 --> countDownLatch
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static void method7() throws ExecutionException, InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(1);
        FutureTask<Integer> futureTask = new FutureTask<>(new MyCallable(null, countDownLatch, null, 2));
        Thread t = new Thread(futureTask);

        long start = System.currentTimeMillis();
        t.start();
        countDownLatch.await();
        int result = futureTask.get();

        // 确保  拿到result 并输出
        System.out.print("method7--> countDownLatch <--");
        System.out.print("异步计算结果为：" + result);
        System.out.println(" 使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }

    /**
     * 方法8 --> cyclicBarrier
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static void method8() throws ExecutionException, InterruptedException {

        //注意这里的parties值为2不为1，才是真正的等子线程运行结束以后主线程获取子线程的运行结果
        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);
        FutureTask<Integer> futureTask = new FutureTask<>(new MyCallable(null, null, cyclicBarrier, 3));
        Thread t = new Thread(futureTask);

        long start = System.currentTimeMillis();
        t.start();
        try {
            //这里也要await,当主线程继续往下的时候，证明子线程已经运算完结果了
            cyclicBarrier.await();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
        int result = futureTask.get();

        // 确保  拿到result 并输出
        System.out.print("method8--> cyclicBarrier <--");
        System.out.print("异步计算结果为：" + result);
        System.out.println(" 使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }

    /**
     * 方法9 --> 阻塞队列
     */
    private static void method9() throws InterruptedException {

        //该阻塞队列容量为1，只能同时放进一个结果
        ArrayBlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<Integer>(1, true);
        Thread t = new Thread(() -> {
            try {
                //将运算好的结果放到阻塞队列中
                blockingQueue.put(sum());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long start = System.currentTimeMillis();
        t.start();
        //该方法是阻塞方法
        int result = blockingQueue.take();

        // 确保  拿到result 并输出
        System.out.print("method9--> blockingQueue <--");
        System.out.print("异步计算结果为：" + result);
        System.out.println(" 使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }

    /**
     * 方法10 --> LockSupport
     * 简LockSupport.unpark(mainThread)方法会给mainThread方法发一张允许执行的许可，而LockSupport.park()方法会消耗掉许可，如果没有票就阻塞当前线程。（“许可”是不能叠加的，“许可”是一次性的）
     */
    private static void method10() throws ExecutionException, InterruptedException {
        Thread mainThread = Thread.currentThread();

        FutureTask<Integer> futureTask = new FutureTask<>(()->{
            int result;
            result = sum();
            LockSupport.unpark(mainThread);
            return result;
        });
        Thread thread = new Thread(futureTask);

        long start = System.currentTimeMillis();
        thread.start();
        //该方法是阻塞方法，若是获取不到许可，则会阻塞当前进程
        LockSupport.park();
        Integer result = futureTask.get();

        // 确保  拿到result 并输出
        System.out.print("method10--> LockSupport <--");
        System.out.print("异步计算结果为：" + result);
        System.out.println(" 使用时间：" + (System.currentTimeMillis() - start) + " ms");
    }
    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if (a < 2) {
            return 1;
        }
        return fibo(a - 1) + fibo(a - 2);
    }


    @AllArgsConstructor
    @NoArgsConstructor
    static class MyCallable implements Callable<Integer> {
        //信号量
        Semaphore semaphore;
        //countDownLatch
        CountDownLatch countDownLatch;
        //cyclicBarrier
        CyclicBarrier cyclicBarrier;
        //callable 类型 -->1:信号量 2:countDownLatch 3:cyclicBarrier 4:阻塞队列
        Integer type;

        @Override
        public Integer call() throws Exception {
            if (!Objects.isNull(type)) {
                switch (type) {
                    case 1:
                        return executeSemaphoreWay(semaphore);
                    case 2:
                        return executeCountDownLatchWay(countDownLatch);
                    case 3:
                        return executeCyclicBarrierWay(cyclicBarrier);
                    default:
                        break;
                }
            }
            return sum();
        }

        /**
         * 按照倒数栅栏的方式进行
         *
         * @param countDownLatch 倒数栅栏
         * @return 返回计算结果
         */
        private Integer executeCountDownLatchWay(CountDownLatch countDownLatch) {
            countDownLatch.countDown();
            return sum();
        }

        /**
         * 按照信号量的方式执行
         *
         * @param semaphore 信号量
         * @return 返回计算结果
         * @throws InterruptedException
         */
        private Integer executeSemaphoreWay(Semaphore semaphore) throws InterruptedException {
            semaphore.acquire();
            int result = sum();
            semaphore.release();
            return result;
        }

        /**
         * 按照循环并发屏障的方式来执行
         *
         * @param cyclicBarrier 循环并发屏障
         * @return
         */
        private Integer executeCyclicBarrierWay(CyclicBarrier cyclicBarrier) throws BrokenBarrierException, InterruptedException {
            //这里计算一定要先写到前面，因为当到await的时候，这时候已经返回结果了
            int result = sum();
            cyclicBarrier.await();
            return result;
        }

    }

    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            sum();
        }
    }

}



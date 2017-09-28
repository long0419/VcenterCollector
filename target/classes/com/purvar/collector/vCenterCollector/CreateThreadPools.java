package com.purvar.collector.vCenterCollector;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 几个常用线程池的理解与运用
 * 
 * @author XIEHEJUN
 * 
 */
public class CreateThreadPools extends Thread {

    @Override
    public void run() {
        System.out.println("系统时间 ： " + System.currentTimeMillis() + " 线程： "
                + Thread.currentThread().getName() + "正在执行！！");

    }

    /**
     * 创建一个单线程的线程池
     * 
     * @return
     */
    public ExecutorService SingleThreadPool() {
        ExecutorService singlePool = Executors.newSingleThreadExecutor();
        return singlePool;
    }

    /**
     * 创建一个大小固定的线程池
     * 
     * @return
     */
    public ExecutorService FixedThreadPool() {
        ExecutorService fixedPool = Executors.newFixedThreadPool(3);
        return fixedPool;
    }

    /**
     * 创建一个可缓存线程池
     * 
     * @return
     */
    public ExecutorService CachedThreadPool() {
        ExecutorService cachedPool = Executors.newCachedThreadPool();
        return cachedPool;
    }

    /**
     * 将创建好的线程放入线程池，并执行
     * 
     * @param pool
     */
    public void service(ExecutorService pool) {
        // 创建线程
        Thread thread1 = new CreateThreadPools();
        Thread thread2 = new CreateThreadPools();
        Thread thread3 = new CreateThreadPools();
        Thread thread4 = new CreateThreadPools();
        Thread thread5 = new CreateThreadPools();
        // 线程入线程池，并执行
        pool.execute(thread1);
        pool.execute(thread2);
        pool.execute(thread3);
        pool.execute(thread4);
        pool.execute(thread5);
        // 关闭线程池
        pool.shutdown();
    }

    /**
     * 创建一个大小无限制的线程池，可用与定时和周期性服务
     */
    public void scheduledThreadPool() {
        ScheduledThreadPoolExecutor scheduledPool = new ScheduledThreadPoolExecutor(1);

        scheduledPool.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                System.out.println("=======" + System.currentTimeMillis()
                        + "=========");
            }
        }, 1000, 5000, TimeUnit.MILLISECONDS);

        scheduledPool.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                System.out.println(System.nanoTime());

            }
        }, 1000, 2000, TimeUnit.MILLISECONDS);

    }

    public static void main(String[] args) {
        CreateThreadPools creatThreadPool = new CreateThreadPools();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("请选择创建线程池：1.单线程线程池；2.可缓存线程池；3.固定大小线程池；4可定时周期性执行线程池");
            int i = sc.nextInt();

            switch (i) {
            case 1:
                System.out.println("-----调用单线程的线程池-----");
                // 调用单线程的线程池
                creatThreadPool.service(creatThreadPool.SingleThreadPool());
                break;
            case 2:
                System.out.println("-----调用可缓存线程的线程池-----");
                // 调用可缓存线程的线程池
                creatThreadPool.service(creatThreadPool.CachedThreadPool());
                break;
            case 3:
                System.out.println("-----调用固定大小线程的线程池-----");
                // 调用固定大小线程的线程池
                creatThreadPool.service(creatThreadPool.FixedThreadPool());
                break;
            case 4:
                System.out.println("-----调用大小无限制可定时和周期性执行的线程池-----");
                // 调用固定大小线程的线程池
                creatThreadPool.scheduledThreadPool();
                break;
            }
        }
    }
}
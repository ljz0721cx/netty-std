package com.janle.std.cases.pools.worker;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lijianzhen1 on 2019/1/15.
 */
public class ByteBufPerformaceTest {

    public static void main(String[] args) {
        //unPoolTest(); //Execute 100000000 times cost time : 69220
        //poolTest();//Execute 100000000 times cost time : 11541

        oldGenTest();
    }



    final static ExecutorService ex= Executors.newFixedThreadPool(4);

    /**
     * 设置到老年代的对象的大小，限制10M直接进老年代
     * -XX:PretenureSizeThreshold=10M
     * CMS收集器
     * -XX:+UseConcMarkSweepGC
     *
     */
    private static final int _1MB = 1024 * 1024;
    private static void oldGenTest() {
        for (int i = 0; i < 10000000; i++) {
            /*byte[] allocation;
            allocation = new byte[50 * _1MB];*/
            ex.submit(()->{
                byte[] allocation;
                allocation = new byte[50 * _1MB];
            });
        }

    }


    //内存池模式
    static void poolTest() {
        PooledByteBufAllocator allocator = new PooledByteBufAllocator(false);
        long beginTime = System.currentTimeMillis();
        ByteBuf buf = null;
        int maxTimes = 100000000;
        for (int i = 0; i < maxTimes; i++) {
            buf = allocator.heapBuffer(10 * 1024);
            buf.release();
        }
        System.out.println("Execute " + maxTimes + " times cost time : "
                + (System.currentTimeMillis() - beginTime));
    }

    //非内存池模式
    static void unPoolTest() {
        long beginTime = System.currentTimeMillis();
        ByteBuf buf = null;
        int maxTimes = 100000000;
        for (int i = 0; i < maxTimes; i++) {
            A a=new A();
            buf = Unpooled.buffer(10 * 1024);
            buf.release();
        }
        System.out.println("Execute " + maxTimes + " times cost time : "
                + (System.currentTimeMillis() - beginTime));
    }


    static class A{
        private String mame;

    }
}

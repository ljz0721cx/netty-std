package com.janle.std.cases.shutdowns;

import sun.misc.Signal;

import java.util.concurrent.TimeUnit;

/**
 * Created by lijianzhen1 on 2018/12/27.
 */
public class JvmSignalHandler {
    public static void main(String[] args) {
        Signal sig = new Signal("INT");
        Signal.handle(sig,(s)->{
            System.out.println("signal handle start ... ");
            try {
                TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            System.out.println("shutdownHook excute start ...");
            System.out.println("Netty NioEventLoopGroup shutdownGracefully ...");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },""));

        new Thread(()->{
            try {
                System.out.println("main中的线程执行");
                TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"Daemon-T").start();
    }
}

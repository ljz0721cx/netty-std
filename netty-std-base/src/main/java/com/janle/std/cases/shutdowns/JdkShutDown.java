package com.janle.std.cases.shutdowns;

import java.util.concurrent.TimeUnit;

/**
 * Created by lijianzhen1 on 2018/12/26.
 */
public class JdkShutDown {
    public static void main(String[] args) throws InterruptedException {
        Runtime.getRuntime().addShutdownHook(new Thread(()->  {
            System.out.println("开始执行shutdown");
            System.out.println("jdk通过 ShutdownHook 优雅关闭");

            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("结束执行shutdown");
        }));
        TimeUnit.SECONDS.sleep(7);
        System.exit(0);
    }
}

package com.janle.std.cases.shutdowns;

import java.util.concurrent.TimeUnit;

/**
 * Created by lijianzhen1 on 2018/12/25.
 */
public class JvmServer {
    public static void main(String[] args) throws InterruptedException {
        long starttime = System.nanoTime();
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    TimeUnit.DAYS.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Daemon-T");
        //这里设置为非守护线程
        t.setDaemon(false);
        t.start();
        TimeUnit.SECONDS.sleep(15);
        System.out.println("系统退出，程序执行" + (System.nanoTime() - starttime) / 1000000000 + "s");
    }
}

package com.janle.std.cases.pools;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by lijianzhen1 on 2018/12/28.
 */
public class InitNettyPool {
    public static void main(String[] args) throws InterruptedException {


        TimeUnit.SECONDS.sleep(10);
        initClientPool(Integer.valueOf(args[0]));


    }


    static void initClientPool(int poolSize) throws InterruptedException {
        for (int i = 0; i < poolSize; i++) {
            EventLoopGroup group = new NioEventLoopGroup();

            //创建不要使用多线程创建。
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new LoggingHandler());
                        }
                    });

            ChannelFuture sync = bootstrap.connect("61.135.169.125", 80).sync();
            sync.channel().closeFuture().addListener((r) -> {
                System.out.println("执行shutdown");
                group.shutdownGracefully();
            });

        }
    }
}

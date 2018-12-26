package com.janle.std.cases.shutdowns;

import com.sun.istack.internal.logging.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 程序意外退出现象
 * Created by lijianzhen1 on 2018/12/25.
 */
public class ExitServer {
    static Logger logger = Logger.getLogger(ExitServer.class);

    public static void main(String[] args) throws InterruptedException {

        final NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        final NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline c = socketChannel.pipeline();
                            c.addLast(new LoggingHandler((LogLevel.INFO)));
                        }
                    });

            ChannelFuture ch = b.bind(8080).sync();
            ///ChannelFuture ch=b.bind(8080).sync(); //使用同步的方式绑定服务监听端口  执行最终调用的是javaChannel().socket().bind(localAddress,config.getBacklog())


            //main线程不是守护线程，Daemon线程在java里面的定义是，如果虚拟机中只有Daemon线程运行，则虚拟机退出
            //设置非守护线程，main退出了，但是jvm并没有退出。
           /* ch.channel().closeFuture().addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    //业务逻辑处理代码，此处省略
                    logger.info(channelFuture.channel().toString()+"链路关闭");
                }
            });
            */

            ch.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    //业务逻辑处理代码，此处省略...
                    logger.info(future.channel().toString() + " 链路关闭");
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            });
            //链路关闭触发closeFuture的监听
            ch.channel().close();

        } finally {
            //bossGroup.shutdownGracefully();
            //workerGroup.shutdownGracefully();
        }
    }
}

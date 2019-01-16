package com.janle.std.cases.pools.oom;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 测试时候的配置
 * -XX:MetaspaceSize=8m -XX:MaxMetaspaceSize=60m -Xmx50m  -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -verbose:gc -XX:+PrintGCTimeStamps -XX:+PrintGCDetails
 * Created by lijianzhen1 on 2019/1/9.
 */
public class RouterClient {
    public static void main(String[] args) throws InterruptedException {
        //客户端访问服务
        EventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new RouterClinetHandler());
                        }
                    });
            // Start the client.
            ChannelFuture f = b.connect(String.valueOf(args[0]), Integer.valueOf(args[1])).sync();

            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}

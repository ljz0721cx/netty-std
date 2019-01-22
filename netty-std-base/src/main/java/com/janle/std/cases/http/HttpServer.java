package com.janle.std.cases.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 提供http的服务
 * Created by lijianzhen1 on 2019/1/16.
 */
public class HttpServer {


    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer();
        int port = Integer.valueOf(args[0]);

        httpServer.bind(port);
    }

    private void bind(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //HTTP解码器可能会将一个HTTP请求解析成多个消息对象。
                            ch.pipeline().addLast(new HttpServerCodec());
                            //HttpObjectAggregator 将多个消息转换为单一的一个FullHttpRequest
                            ch.pipeline().addLast(new HttpObjectAggregator(Short.MAX_VALUE));
                            //
                            ch.pipeline().addLast(new HttpServerHandler());
                        }
                    });

            ChannelFuture f = b.bind("127.0.0.1", port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

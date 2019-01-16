package com.janle.std.cases.http;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.DefaultPromise;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * args 127.0.0.1 8080
 * Created by lijianzhen1 on 2019/1/16.
 */
public class HttpClient {

    public static void main(String[] args) throws InterruptedException, UnsupportedEncodingException, ExecutionException {
        HttpClient httpClient = new HttpClient();
        httpClient.connect(String.valueOf(args[0]), Integer.valueOf(args[1]));
        ByteBuf body = Unpooled.wrappedBuffer("HttpClient请求消息".getBytes("UTF-8"));
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "http://127.0.0.1/user?id=10&addr=山西", body);
        HttpResponse response = httpClient.blockSend(request);
    }

    /**
     * 阻塞发送
     *
     * @param request
     * @return
     */
    private HttpResponse blockSend(DefaultFullHttpRequest request) throws ExecutionException, InterruptedException {
        request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
        //获取线程执行的结果信息
        DefaultPromise<HttpResponse> respPromise = new DefaultPromise<>(channel.eventLoop());
        //设置Promise
        handler.setRespPromise(respPromise);
        channel.writeAndFlush(request);
        HttpResponse response = respPromise.get();
        if (response != null) {
            System.out.println("客户端请求http响应结果：" + new String(response.body()));
        }
        return response;
    }


    private Channel channel;

    HttpClientHandler handler = new HttpClientHandler();

    private void connect(String host, int port) throws InterruptedException {
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        Bootstrap b = new Bootstrap();
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new HttpClientCodec());
                ch.pipeline().addLast(new HttpObjectAggregator(Short.MAX_VALUE));
                ch.pipeline().addLast(handler);
            }
        });
        ChannelFuture f = b.connect(host, port).sync();
        channel = f.channel();
    }
}

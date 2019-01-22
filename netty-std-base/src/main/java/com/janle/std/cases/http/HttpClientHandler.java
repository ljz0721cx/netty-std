package com.janle.std.cases.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.DefaultPromise;

/**
 * 处理http响应参数的的处理Handler
 * Created by lijianzhen1 on 2019/1/16.
 */
public class HttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    DefaultPromise<HttpResponse> respPromise;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) throws Exception {
        if (msg.decoderResult().isFailure())
            throw new Exception("decoder HttpResponse error:" + msg.decoderResult().cause());
        HttpResponse response = new HttpResponse(msg);
        //这里还没有释放对象
        System.out.println("response unchannlRead "+ new String(response.body()));
        respPromise.setSuccess(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public DefaultPromise<HttpResponse> getRespPromise() {
        return respPromise;
    }

    public void setRespPromise(DefaultPromise<HttpResponse> respPromise) {
        this.respPromise = respPromise;
    }
}

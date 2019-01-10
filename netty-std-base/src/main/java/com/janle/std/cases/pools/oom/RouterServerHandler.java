package com.janle.std.cases.pools.oom;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ReflectionUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lijianzhen1 on 2019/1/8.
 */
public class RouterServerHandler extends ChannelInboundHandlerAdapter {
    static ExecutorService executorService = Executors.newSingleThreadExecutor();
    PooledByteBufAllocator allocator = new PooledByteBufAllocator(false);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf regMsg=(ByteBuf)msg;
         byte[] body=new byte[regMsg.readableBytes()];
        //ReferenceCountUtil.release(regMsg);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                ByteBuf respMsg=allocator.heapBuffer(body.length);
                //将请求直接转化为响应
                respMsg.writeBytes(body);
                ctx.writeAndFlush(respMsg);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

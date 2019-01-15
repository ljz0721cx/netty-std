package com.janle.std.cases.pools.oom;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lijianzhen1 on 2019/1/15.
 */
public class SimpServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
    static ExecutorService executorService = Executors.newSingleThreadExecutor();
    PooledByteBufAllocator allocator = new PooledByteBufAllocator(false);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        ByteBuf regMsg = (ByteBuf) msg;
        byte[] body = new byte[msg.readableBytes()];
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                ByteBuf respMsg = allocator.heapBuffer(body.length);
                //将请求直接转化为响应
                respMsg.writeBytes(body);
                ctx.writeAndFlush(respMsg);
                //ctx.fireChannelRead(msg);
            }
        });
    }
}

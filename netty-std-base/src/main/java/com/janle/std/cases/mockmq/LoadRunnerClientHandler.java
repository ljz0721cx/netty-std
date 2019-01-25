package com.janle.std.cases.mockmq;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by lijianzhen1 on 2019/1/22.
 */
public class LoadRunnerClientHandler extends ChannelInboundHandlerAdapter {
    private final ByteBuf firstMessage;
    Runnable loadRunner;

    AtomicLong sendSum = new AtomicLong(0);
    Runnable profileMonitor;

    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    /**
     * Creates a client-side handler.
     */
    public LoadRunnerClientHandler() {
        firstMessage = Unpooled.buffer(SIZE);
        for (int i = 0; i < firstMessage.capacity(); i++) {
            firstMessage.writeByte((byte) i);
        }
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        //这里限制高水位,如果不限制直接将服务器压垮了
        ctx.channel().config().setWriteBufferHighWaterMark(10 * 1024 * 1024);
        loadRunner = new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ByteBuf msg = null;
                final int len = "Netty OOM Example".getBytes().length;
                while (true) {
                    //判断是否越过高水位
                    if (ctx.channel().isWritable()) {
                        msg = Unpooled.wrappedBuffer("Netty OOM Example".getBytes());
                        //这里会有问题
                        ctx.writeAndFlush(msg);
                    } else {
                        //System.out.println("写入队列已经满了对应buffer的大小 :" + ctx.channel().unsafe().outboundBuffer().nioBufferCount());
                    }
                }
            }
        };
        new Thread(loadRunner, "LoadRunner-Thread").start();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}

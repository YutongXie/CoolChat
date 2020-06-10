package com.huitong.coolchat.handler;

import com.huitong.coolchat.protocol.CoolChatProtocol;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class CoolChatServerHandler extends SimpleChannelInboundHandler<String> {

    private static final ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:DD");
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        Channel fromChannel = channelHandlerContext.channel();
//        String content = new String(coolChatProtocol.getContent(), CharsetUtil.UTF_8);
        log.info("Server received message from client:{}", msg);
        group.forEach(channel -> {

            if(channel == fromChannel) {
                channel.pipeline().writeAndFlush("[From me]-" + msg);
            } else {
                channel.pipeline().writeAndFlush("[From" + channel.remoteAddress() + "]-" + msg);
            }
        });
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel newChannel = ctx.channel();
        group.forEach(channel -> {
            channel.pipeline().writeAndFlush("Client " + newChannel.remoteAddress() + " is online at " + sdf.format(new Date()));
        });
        group.add(newChannel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel quitChannel = ctx.channel();
        group.forEach(channel -> {
            channel.pipeline().writeAndFlush("Client " + quitChannel.remoteAddress() + " is offline at " + sdf.format(new Date()));
        });
        group.remove(quitChannel);
    }
}

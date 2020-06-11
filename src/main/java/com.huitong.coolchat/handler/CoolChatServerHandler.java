package com.huitong.coolchat.handler;

import com.huitong.coolchat.protocol.CoolChatMessage;
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
public class CoolChatServerHandler extends SimpleChannelInboundHandler<CoolChatMessage> {

    private static final ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:DD");
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CoolChatMessage msg) throws Exception {
        Channel fromChannel = channelHandlerContext.channel();
        int length = msg.getLength();
        String content = new String(msg.getContent(), CharsetUtil.UTF_8);
        log.info("Server received message from client- length:{}, content:{}", length, content);
        group.forEach(channel -> {
            if(channel == fromChannel) {
                CoolChatMessage newMsg = new CoolChatMessage();
                newMsg.setContent(("[From me]-" + content).getBytes());
                newMsg.setLength(("[From me]-" + content).getBytes().length);
                channel.pipeline().writeAndFlush(newMsg);
            } else {
                CoolChatMessage newMsg = new CoolChatMessage();
                newMsg.setContent(("[From" + channel.remoteAddress() + "]-" + content).getBytes());
                newMsg.setLength(("[From" + channel.remoteAddress() + "]-" + content).getBytes().length);
                channel.pipeline().writeAndFlush(newMsg);
            }
        });
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel newChannel = ctx.channel();
        group.forEach(channel -> {
            String msg = "Client " + newChannel.remoteAddress() + " is online at " + sdf.format(new Date());
            CoolChatMessage newMsg = new CoolChatMessage();
            newMsg.setContent(msg.getBytes());
            newMsg.setLength(msg.getBytes().length);
            channel.pipeline().writeAndFlush(newMsg);
        });
        group.add(newChannel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel quitChannel = ctx.channel();
        group.forEach(channel -> {
            String msg = "Client " + quitChannel.remoteAddress() + " is offline at " + sdf.format(new Date());
            CoolChatMessage newMsg = new CoolChatMessage();
            newMsg.setContent(msg.getBytes());
            newMsg.setLength(msg.getBytes().length);
            channel.pipeline().writeAndFlush(newMsg);
        });
        group.remove(quitChannel);
    }
}

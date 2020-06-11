package com.huitong.coolchat.handler;

import com.huitong.coolchat.protocol.CoolChatMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CoolChatClientHandler extends SimpleChannelInboundHandler<CoolChatMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, CoolChatMessage msg) throws Exception {
        log.info(new String(msg.getContent(), CharsetUtil.UTF_8));
    }
}

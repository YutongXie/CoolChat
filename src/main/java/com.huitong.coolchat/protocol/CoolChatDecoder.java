package com.huitong.coolchat.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
public class CoolChatDecoder extends ByteToMessageDecoder {

    private int length = 0;
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        if(byteBuf.readableBytes() > 4) {
            length = byteBuf.readInt();
        }

        if(byteBuf.readableBytes() < length) {
            log.info("not received full message. waiting..");
            return;
        }

        if(byteBuf.readableBytes() >= length) {
            byte[] content = new byte[length];
            byteBuf.readBytes(content);
            CoolChatMessage message = new CoolChatMessage();
            message.setLength(length);
            message.setContent(content);
            list.add(message);
        }
        length = 0;
    }
}

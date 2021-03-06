package com.huitong.coolchat.client;

import com.huitong.coolchat.handler.CoolChatClientHandler;
import com.huitong.coolchat.protocol.CoolChatDecoder;
import com.huitong.coolchat.protocol.CoolChatEncoder;
import com.huitong.coolchat.protocol.CoolChatMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class CoolChatClient2 {

    public void connect(String hostName, int port) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("decoder", new CoolChatDecoder());
                            socketChannel.pipeline().addLast("encoder", new CoolChatEncoder());
                            socketChannel.pipeline().addLast(new CoolChatClientHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(hostName, port).sync();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                CoolChatMessage msg = new CoolChatMessage();
                msg.setLength(message.getBytes().length);
                msg.setContent(message.getBytes());
                channelFuture.channel().writeAndFlush(msg);
            }
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        CoolChatClient2 client = new CoolChatClient2();
        try {
            client.connect("localhost", 9009);
        } catch (InterruptedException e) {
            log.error("failed to connect to server -", e);
        }
    }
}

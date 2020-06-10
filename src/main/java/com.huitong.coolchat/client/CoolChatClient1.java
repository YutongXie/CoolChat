package com.huitong.coolchat.client;

import com.huitong.coolchat.handler.CoolChatClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;
@Slf4j
public class CoolChatClient1 {

    public void connect(String hostName, int port) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("decoder", new StringDecoder());
                            socketChannel.pipeline().addLast("encoder", new StringEncoder());
                            socketChannel.pipeline().addLast(new CoolChatClientHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(hostName, port).sync();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                channelFuture.channel().writeAndFlush(message);
            }
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        CoolChatClient1 client = new CoolChatClient1();
        try {
            client.connect("localhost", 9009);
        } catch (InterruptedException e) {
            log.error("failed to connect to server -", e);
        }
    }
}

package com.jaymes.server;

import com.jaymes.handler.TimeServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author Elias Jiang
 * @date 2020/11/15
 */
public class DiscardServer {

  private int port;

  public DiscardServer(int port) {
    this.port = port;
  }

  public static void main(String[] args) throws Exception {
    int port = 8080;
    if (args.length > 0) {
      port = Integer.parseInt(args[0]);
    }

    new DiscardServer(port).run();
  }

  public void run() throws Exception {
    // NioEventLoopGroup is a multithreaded event loop that handles I/O operation.
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      //ServerBootstrap is a helper class that sets up a server
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
          // instantiate a new Channel to accept incoming connections.
          .channel(NioServerSocketChannel.class)
          // ChannelInitializer is a special handler that is purposed
          // to help a user configure a new Channel.
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
              //ch.pipeline().addLast(new DiscardServerHandler());
              ch.pipeline().addLast(new TimeServerHandler());
            }
          })
          // set the parameters which are specific to the Channel implementation
          // option() is for the NioServerSocketChannel that accepts incoming connections.
          // childOption() is for the Channels accepted by the parent ServerChannel,
          // which is NioServerSocketChannel in this case.
          .option(ChannelOption.SO_BACKLOG, 128)
          .childOption(ChannelOption.SO_KEEPALIVE, true);

      // Bind and start to accept incoming connections.
      ChannelFuture f = b.bind(port).sync(); // (7)

      // Wait until the server socket is closed.
      // In this example, this does not happen, but you can do that to gracefully
      // shut down your server.
      f.channel().closeFuture().sync();
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }
}

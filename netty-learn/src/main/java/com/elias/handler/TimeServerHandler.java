package com.elias.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


/**
 * The protocol to implement in this section is the TIME protocol. It is different from the previous
 * examples in that it sends a message, which contains a 32-bit integer, without receiving any
 * requests and closes the connection once the message is sent. In this example, you will learn how
 * to construct and send a message, and to close the connection on completion.
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

  /**
   * Because we are going to ignore any received data but to send a message as soon as a connection
   * is established, we cannot use the channelRead() method this time. Instead, we should override
   * the channelActive() method.
   *
   * @param ctx
   */
  @Override
  //method will be invoked when a connection is established and ready to generate traffic.
  public void channelActive(final ChannelHandlerContext ctx) { // (1)
    final ByteBuf time = ctx.alloc().buffer(4); // (2)
    time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

    // A ChannelFuture represents an I/O operation which has not yet occurred.
    // Alternatively, you could simplify the code using a pre-defined listener:
    // f.addListener(ChannelFutureListener.CLOSE);
    final ChannelFuture f = ctx.writeAndFlush(time);
    f.addListener(new ChannelFutureListener() {
      // need to call the close() method after the ChannelFuture is complete
      @Override
      public void operationComplete(ChannelFuture future) {
        assert f == future;
        ctx.close();
      }
    }); // (4)
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }
}
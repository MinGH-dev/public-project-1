package com.gh.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class SocketClientHandler extends SimpleChannelInboundHandler<String>  {	
    private volatile Channel channel;
    private volatile long sendCount = 0;
    private volatile long recvCount = 0;

    /**
     * Creates a client-side handler.
     */
    public SocketClientHandler() {
    	super();     
    }
    
    public boolean SendMessage( String jstr ) {
    	channel.writeAndFlush(jstr+"\n");
    	return true;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
    	channel = ctx.channel(); //ä�� �Ҵ�.
    }
    
    @Override 
    public void channelInactive(ChannelHandlerContext ctx) {
//		System.out.println( "------------channel inactive------- " );
		if (SocketClient.f != null) {
			SocketClient.f = null;
		}
		
		if (SocketClient.group != null) {
			SocketClient.group.shutdownGracefully();
			SocketClient.group.terminationFuture();
		}

    }
    
	@Override
    public void channelRead0(ChannelHandlerContext ctx, String msg)  {
    	recvCount++;
    	
//	    System.out.println( "send :" + sendCount + " recvCount="+recvCount );
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
       ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    
}

package com.gh.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.CharsetUtil;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import org.json.simple.JSONObject;

import com.gh.common.Commons;
import com.gh.common.LogLevel;
import com.gh.common.util.CommonUtil;
import com.gh.process.abstracts.AbstractQueue;

public class SocketServer {

	EventLoopGroup bossGroup = new NioEventLoopGroup();  // netty ����
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    ChannelFuture f;
    
    private AbstractQueue<String> queue = null;
    
    public SocketServer (AbstractQueue<String> queue) {
    	this.queue = queue;
    }
    
	public ServerBootstrap bootstrap() throws SSLException, CertificateException{
		
		final boolean SSL = System.getProperty("ssl") != null;
		final SslContext sslCtx;
		if (SSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
		} else {
			sslCtx = null;
		}
		
		ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
        //b.group( workerGroup) 
         .channel(NioServerSocketChannel.class)
         .option(ChannelOption.SO_RCVBUF, 1000000000)
         .option(ChannelOption.SO_BACKLOG, 1000000000)
         .handler(new LoggingHandler(io.netty.handler.logging.LogLevel.INFO))
         .childHandler(new ChannelInitializer<SocketChannel>() {
             @Override
             public void initChannel(SocketChannel ch) throws Exception {
                 ChannelPipeline p = ch.pipeline();
                 if (sslCtx != null) {
                     p.addLast(sslCtx.newHandler(ch.alloc()));
                 }
                 p.addLast(
//                         new yCommServerDecoder() ,
                		 new LineBasedFrameDecoder(1000000000),
                         new StringDecoder(CharsetUtil.UTF_8),
                		 new StringEncoder(CharsetUtil.UTF_8),                             
//                         new ChunkedWriteHandler(),
//                		 new ObjectDecoder(ClassResolvers.softCachingResolver(ClassLoader.getSystemClassLoader())),
//                		 new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
//                         new ObjectEncoder(),
                		 new SocketServerHandler(queue));
             }
        });
        
        return b;
	}
	public ChannelFuture getChannelFuture(){
		return f;
	}
	
	
	public void start() {
		 // Start the server.
        
		try {
			f = bootstrap().bind("localHost", Commons.socketPort).sync();
			
			CommonUtil.println("Listening port=" + Commons.socketPort , LogLevel.SYSTEM) ;	
			
			// Wait until the server socket is closed.
			f.channel().closeFuture().sync();
		} catch (SSLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
        	workerGroup.shutdownGracefully();
        }
	}
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}

}

package com.gh.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.json.simple.JSONObject;

import com.gh.common.config.ClientConfig;
import com.gh.common.context.PropsKey;
import com.gh.common.util.CommonUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;

public class SocketClient {
	static final boolean SSL = System.getProperty("ssl") != null;
    static String LOG_FILE_PATH; 
    static boolean SAVE_EXCEL;
    static long SKIP_LINE;
    static boolean TEST_DUMMY;
    static long TPS;
    
    static EventLoopGroup group = null;
    static ChannelFuture f = null;
    static SocketClientHandler handler = null;
    static Bootstrap b = null;
    public static long initCount = 0;
    
    private static SocketClient instance = null;
    private static int timeOut = 0;
    public static int FailDataSaveCount = 0;
    
    public static SocketClient getInstance() {
    	if (instance == null || f == null) {
    		instance = new SocketClient();
    	}
    	return instance;
    }
    
    private SocketClient() {
    	init();
    }
    
    public static void init() {
    	initCount++;
    	
    	if (initCount == 1) {
    		timeOut = 1000;
    	} else {
    		timeOut = 1;
    	}
    	
    	if (group != null) {
			group.shutdownGracefully();
			group.terminationFuture();
		}
    	
    	// Configure the Client.
    	group = new NioEventLoopGroup();
    	try {
    		
    		final SslContext sslCtx;
    		if (SSL) {
    			sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
    		} else {
    			sslCtx = null;
    		}
    		
    		b = new Bootstrap();
    		
    		b.group(group)
            .channel(NioSocketChannel.class)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOut)
            .option(ChannelOption.SO_RCVBUF, 1000000000)
            .option(ChannelOption.SO_SNDBUF, 1000000000)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    if (sslCtx != null) {
                        p.addLast(sslCtx.newHandler(ch.alloc(), ClientConfig.getConfig().getProps(PropsKey.SERVER_HOST), Integer.parseInt(ClientConfig.getConfig().getProps(PropsKey.SERVER_PORT))));
                    }
                    p.addLast(
                   		 new StringEncoder(CharsetUtil.UTF_8),
                         new LineBasedFrameDecoder(1000000000),
                         new StringDecoder(CharsetUtil.UTF_8),
                   		 new SocketClientHandler());
                }
            });
           
           // Start the client.
           try {
        	   f = b.connect(new InetSocketAddress(ClientConfig.getConfig().getProps(PropsKey.SERVER_HOST), Integer.parseInt(ClientConfig.getConfig().getProps(PropsKey.SERVER_PORT)))).sync();
           } catch (Exception e) {
           }
           
           if (f != null) {
        	   if (f.isSuccess()) {
	    		   handler = f.channel().pipeline().get(SocketClientHandler.class);
	    	   }
           } else {
        	   // System.out.println("ChannelFuture is null");
           }
    	   
    	} catch (IOException e) {
	    	e.printStackTrace();
	    }
    }
	
	public void sendMessage(String message){
		try{
			ClientRun(message);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	private static void ClientRun(String msg) throws Exception{
    	
        try {
            if (f != null && f.isSuccess()) {
            	handler.SendMessage(msg);
            } else {
            	File dir = new File("sendFailData");
            	if (!dir.exists()) {
            		dir.mkdir();
            	}
            	
            	String fileDate = CommonUtil.getNow("yyyyMMddHHmm");
            	fileDate = String.format("%s0", fileDate.substring(0, fileDate.length() - 1));
            	File file = new File(dir.getPath() + "/" + fileDate + ".log");
            	if (!file.exists()) {
            		file.createNewFile();
            	}
            	
            	FileOutputStream fos = null;
            	try {
            		fos = new FileOutputStream(file, true);
            		fos.write((msg+"\n").getBytes());
            	} catch (Exception e) {
            		e.printStackTrace();
            	} finally {
            		if (fos != null) {
            			fos.close();
            		}
            		file = null;
            	}
            	++FailDataSaveCount;
            }
        } catch (Exception e) {
        	e.printStackTrace();
		}  finally {
			msg = null;
		}
	}
}

package com.gh.server;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import com.gh.common.LogLevel;
import com.gh.common.util.CommonUtil;
import com.gh.process.abstracts.AbstractQueue;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;

public class SocketServerHandler extends SimpleChannelInboundHandler<String> {
	private static long activeCount = 0;
	private static long inactiveCount = 0;
	public boolean IsClosed = false;
	
	@SuppressWarnings("unused")
	private volatile Channel channel;
	@SuppressWarnings("unused")
	private EventLoopGroup workGroup;
	// ByteBuf buf ;
	// StringBuffer buf ;
	// Executor executor =
	// Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
	// ;//Executors.newCachedThreadPool() ; //실제 사용할때마다 생성하는 쓰레드 풀, 이미 생성된건 재활용
	// long start ;
	public static long count = 0;
	AbstractQueue<String> queue = null;
	
	public static long detectorReceiveCount = 0;

	public SocketServerHandler(AbstractQueue<String> queue) {
		this.queue = queue;
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) {
		// buf = ctx.alloc().buffer(4); // (1)
		// buf = ctx.alloc().buffer(500) ;
		// buf = new StringBuffer() ;
		CommonUtil.println("handlerAdded-------------", LogLevel.DEBUG);
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) {
		// buf.release();
		// buf = null;
		CommonUtil.println("handlerRemoved-------------", LogLevel.DEBUG);
	}
	
	public void CloseNetty(){
		this.channel.writeAndFlush("Close");
	}
	/*
	 * public void channelRead(ChannelHandlerContext ctx, Object msg) { ByteBuf
	 * m = (ByteBuf)msg ;
	 * 
	 * int len = m.readableBytes() ; byte[] bt = new byte[len] ; for( int i=0 ;
	 * i < len ; i++ ){ bt[i] = m.readByte() ; } String str = new String( bt,
	 * Charset.forName("US-ASCII") ) ;
	 * 
	 * FdsUtil.DebugPrt( "len = " + len ) ;
	 * 
	 * FdsUtil.DebugPrt( str ) ;
	 * 
	 * m.release() ;
	 * 
	 * }
	 */
	/*
	 * @Override public void channelRead(ChannelHandlerContext ctx, Object msg)
	 * { //String recvData = "" ; ByteBuf m = (ByteBuf) msg ;
	 * 
	 * buf.writeBytes(m) ; m.release() ;
	 * 
	 * int len = buf.readableBytes() ; FdsUtil.DebugPrt( "len = " + len ) ; if(
	 * len > 0 ){ int idx = buf.indexOf(0, len, (byte)'}') ; if( idx > 0){
	 * byte[] bt = new byte[idx+1] ;
	 * 
	 * for( int i=0 ; i < idx+1 ; i++) bt[i] = buf.readByte() ;
	 * //System.out.print(buf.readChar() );
	 * 
	 * String str = new String(bt,Charset.forName("US-ASCII") ) ;
	 * FdsUtil.DebugPrt(str) ; ctx.close() ; } }
	 * 
	 * 
	 * //FdsUtil.DebugPrt( recvData ) ;
	 * 
	 * }
	 */

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		System.out.println("------------channel active------- : " + ctx.name());
		// channel = ctx.channel() ; //채널 할당.
		// ClientChannel sd = ClientChannel.getInstance();
		// sd.setChannel(channel);
		// sd.setHandler(ctx.channel().pipeline().get
		// pipeline().get(yCommServerHanlder.class) );
		// start = System.currentTimeMillis();
		count = 0;
		
		activeCount++;
		
		JSONObject obj = new JSONObject();
		obj.put("connChannelName", ctx.name());
		obj.put("channelCode", "1");
		obj.put("isAuto", "Y");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		ctx.flush();
		// long spent = System.currentTimeMillis() - start ;
		// FdsUtil.DebugPrt( "------------channel inactive------- time spent= "
		// + (spent / 1000) + " , count = " + count );
		System.out.println("------------channel inactive------- : " + ctx.name());
		
		inactiveCount++;
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, String msg) {
		count++;
		// FdsUtil.DebugPrt( "count="+count +" : --recvData = " + msg ) ;

		// TransLog tl = jsonToTransLog(msg) ;
		// System.out.println("read jsonStr" + msg);
		if (msg != null) {
			try {
				queue.add("another : " + msg);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				CommonUtil.println(e);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				CommonUtil.println(e);
			}
		}
	}


	// public boolean SendTransLog(String jstr){
	// channel.writeAndFlush(jstr + "\n") ;
	// return true ;
	// }

	/*
	 * TransLog jsonToTransLog( String json ){ TransLog tl = new TransLog() ;
	 * JSONObject obj = null ; try { obj =
	 * (JSONObject)JSONValue.parseWithException(json); } catch (ParseException
	 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * if( obj != null ){ Set jsonKey = obj.keySet(); Iterator itKey =
	 * jsonKey.iterator(); FdsUtil.DebugPrt( "json translate ") ; String channel
	 * = (String)obj.get("channel") ; tl.setChannelCd(channel) ;
	 * 
	 * while( itKey.hasNext()){ String key = (String)itKey.next(); String value
	 * = (String)obj.get(key); System.out.print(", key : " + key + ", value : "
	 * + value);
	 * 
	 * switch(key){ case "logSvcCd" : tl.setLogSvcCd(value); break ; //case
	 * "channel" : tl.setChannelCd(value) ; break ; case "userId" :
	 * tl.setUserId(value) ; break ; case "logPayBcd" : tl.setLogPayBcd(value) ;
	 * break ; case "logRcvBcd" : tl.setLogRcvBcd( value) ; break ; case
	 * "logRcvAcc" : tl.setLogRcvAcc( value) ; break ; case "logAmt" :
	 * tl.setLogAmt( Long.parseLong(value) ) ; break ; }
	 * 
	 * //pc if( "1".equals(channel )){ TransLogTypePC logPc = new
	 * TransLogTypePC() ; switch(key){ case "ipEth0" : logPc.setIpEth0(value) ;
	 * break ; case "cntryCd" : logPc.setCntryCd(value) ; break ; case "macEth0"
	 * : logPc.setMacEth0(value) ; break ; case "hddSerial" :
	 * logPc.setHddSerial(value) ; break ; case "vpnYN" : logPc.setVpnYN(
	 * value.charAt(0) ) ; break ; case "prxyYN" :
	 * logPc.setPrxyYN(value.charAt(0) ) ; break ; case "winOsRmtYN" :
	 * logPc.setWinOsRmtYN(value.charAt(0) ) ; break ; case "osLangCd" :
	 * logPc.setOsLangCd(value); break ; case "stsDhack" :
	 * logPc.setStsDhack(value); break ;
	 * 
	 * } tl.setLogPC(logPc); }
	 * 
	 * 
	 * 
	 * } return tl ;
	 * 
	 * }
	 * 
	 * FdsUtil.DebugPrt("jsonparse error : " + json ) ; return null ;
	 * 
	 * }
	 */

	// @Override
	// public void channelReadComplete(ChannelHandlerContext ctx) {
	// ctx.flush();
	// }

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		System.out.println(cause.getStackTrace());
		ctx.close();
	}
}

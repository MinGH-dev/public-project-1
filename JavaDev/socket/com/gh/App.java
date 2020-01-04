package com.gh;

import com.gh.chat.Console;
import com.gh.common.config.ClientConfig;
import com.gh.common.util.CommonUtil;
import com.gh.process.abstracts.AbstractQueue;
import com.gh.process.abstracts.AbstractThread;
import com.gh.server.SocketServer;

public class App {
	public static void main (String[] args) {
		ClientConfig.getConfig();
		
		
		AbstractQueue<String> queue = new AbstractQueue<String>("room1_queue");
		AbstractThread t1 = new AbstractThread("room1", 1, queue) {
			@Override
			public void process(String msg) throws Exception {
				// TODO Auto-generated method stub
				System.out.println("[" + CommonUtil.getNow("MM-dd HH:mm") + "] " + msg);
			}
		};
		t1.start();
		System.out.println("room1 start Succeeded");
		
		Console console = new Console(queue);
		console.start();
		System.out.println("Console Run Succeeded");
		
		SocketServer socketServer = new SocketServer(queue);
		socketServer.start();
	}
}

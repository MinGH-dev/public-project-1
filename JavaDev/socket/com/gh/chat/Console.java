package com.gh.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.gh.client.SocketClient;
import com.gh.process.abstracts.AbstractQueue;

public class Console extends Thread {
	
	private AbstractQueue<String> queue = null;
	
	public Console (AbstractQueue<String> queue) {
		this.queue = queue;
	}
	
	@Override
	public void run () {
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		try {
			while (true) {
				try {
					String msg;
					if ((msg = br.readLine()) != null) {
						queue.add("me : " + msg);
						SocketClient.getInstance().sendMessage(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Thread.sleep(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

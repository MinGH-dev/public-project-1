package com.gh.process.abstracts;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.gh.common.util.CommonUtil;

public abstract class AbstractThread extends Thread {
	protected AbstractQueue<String> inputQueue = null;
	protected long sleepTime = 1;
	protected String lastExecuteTime = CommonUtil.getNow("yyyyMMddHHmmssSSS");
	
	public AbstractThread (String name, long sleepTime, AbstractQueue<String> inputQueue) {
		this.setName(name);
		this.sleepTime = sleepTime;
		this.inputQueue = inputQueue;
	}
	
	
	@Override
	public void run () {
		try {
			
			while (true) {
				try {
					if (this.inputQueue != null && this.inputQueue.GetQueSize() > 0) {
						process(inputQueue.get());
					}
				} catch (Exception e) {
					CommonUtil.println(e);
				} finally {
					try {
						if (this.inputQueue != null && this.
								inputQueue.GetQueSize() > 0) {
							Thread.sleep(1);
						} else {
							Thread.sleep(sleepTime);
						}
						
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			CommonUtil.println(e);
		}
	}
	
	public abstract void process (String msg) throws Exception ;
}

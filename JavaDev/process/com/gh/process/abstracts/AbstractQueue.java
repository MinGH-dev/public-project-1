package com.gh.process.abstracts;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.gh.common.LogLevel;
import com.gh.common.util.CommonUtil;


public class AbstractQueue<E> {
	private LinkedBlockingQueue<E> queue = new LinkedBlockingQueue<E>() ;//new ArrayBlockingQueue<JSONObject>(500000); 
	private volatile long addCount=0 ;
	private volatile long getCount=0 ;
	
	private String name = null;
	
	public AbstractQueue (String name) {
		this.name = name;
	}
	
	public LinkedBlockingQueue<E> getQueObject () {
		return queue;
	}
	
	//public synchronized boolean add(TransLog tl ){
	public boolean add(E data ) throws Exception{
		// TODO set collect status to memory database 
		
//		if (addCount%100 == 0) {
//			System.out.println("TransLogToDbQueue add Count : " + addCount);
//		}
		if (data == null) {
			return false;
		}
		
		if (Long.MAX_VALUE == addCount) {
			ResetCount();
		}
		
		addCount++ ;
		
		if (addCount % 1000 == 0) {
			printCounts();
		}
		
		return queue.add(data) ;
	}
	
	public long getAddCount() {
		return addCount;
	}
	
	public void setAddCount(long value) {
		addCount += value;
	}

	public long getGetCount() {
		return getCount;
	}
	
	public void setGetCount(long value) {
		getCount += value;
	}

	public void ResetCount(){
		addCount = 0;
		getCount = 0;
	}
	
	public int GetQueSize(){
		return queue.size();
	}
	
	public void printCounts() throws Exception{
		CommonUtil.println("-- "+ name +" get=" + getCount + " , add=" + addCount  + " que size=" + queue.size(), LogLevel.SYSTEM) ;
	}
	
	//public synchronized TransLog getLoop(){
	public E get(){
		try {
			E data = queue.poll(10, TimeUnit.SECONDS) ;//10珥덈룞�븞 poll�씠 �뾾�쑝硫� null 由ы꽩//queue.take() ;
			if( data != null )
				getCount++ ;
			
			return data ;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return null ;
	}
}

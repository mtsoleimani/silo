/*
 *  Copyright 2018 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package io.cassandana.silo.dispatcher;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import io.cassandana.silo.database.DatabaseOperation;
import io.cassandana.silo.scheduler.IScheduler;

public class Worker implements IScheduler, Runnable {
	
	private ConcurrentLinkedQueue<Message> queue;
	
	private BlockingQueue<Object> taskQueue;
	
	private long lastTask = 0;
	
	private AtomicInteger queueSize;
	
	private int thresholdCount; 
	
	private int thresholdTime;
	
	private DatabaseOperation database;
	
	private boolean hasBulk;
	
	public void stop() {
		taskQueue.add(null);
	}
	
	public Worker(int thresholdCount, int thresholdTime, DatabaseOperation database, boolean hasBulk) {
		this.thresholdCount = thresholdCount;
		this.thresholdTime = thresholdTime;
		this.hasBulk = hasBulk;
		queue = new ConcurrentLinkedQueue<>();
		taskQueue = new LinkedBlockingQueue<>();
		queueSize = new AtomicInteger(0);
		
		this.database = database; 
		
	}
	
	public void enqueue(Message message) {
		
		if(!hasBulk) {
			database.insert(message);
			return;
		}
		
		queue.add(message);
		int size = queueSize.incrementAndGet();
		
		if(size >= thresholdCount)
			taskQueue.add(new Object());
	}

	@Override
	public void onTimer() {
		if(hasBulk && System.currentTimeMillis() - lastTask >= thresholdTime)
			taskQueue.add(new Object());
	}

	@Override
	public void run() {
		
		while(true) {
			
			Object task = null;
			try {
				task = taskQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(task == null)
				break;
			
			execute();
		}
		
	}

	
	private void execute() {
		
		int size = queueSize.get();
		int readSize = thresholdCount;
		if(size < thresholdCount)
			readSize = size;
		
		ArrayList<Message> list = new ArrayList<>();
		for(int i = 0; i<readSize; i++) {
			Message message = queue.poll();
			if(message != null) {
				list.add(message);
				queueSize.decrementAndGet();
			} else {
				break;
			}
		}
		
		database.bulkInsert(list);
		lastTask = System.currentTimeMillis();
	}
	
}

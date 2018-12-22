package ir.mqtt.silo.dispatcher;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import ir.mqtt.silo.database.DatabaseOperation;
import ir.mqtt.silo.scheduler.IScheduler;

public class Worker implements IScheduler, Runnable {
	
	private ConcurrentLinkedQueue<Message> queue;
	
	private BlockingQueue<Object> taskQueue;
	
	private long lastTask = 0;
	
	private AtomicInteger queueSize;
	
	private int thresholdCount; 
	
	private int thresholdTime;
	
	private DatabaseOperation database;
	
	public void stop() {
		taskQueue.add(null);
	}
	
	public Worker(int thresholdCount, int thresholdTime, DatabaseOperation database) {
		this.thresholdCount = thresholdCount;
		this.thresholdTime = thresholdTime;
		
		queue = new ConcurrentLinkedQueue<>();
		taskQueue = new LinkedBlockingQueue<>();
		queueSize = new AtomicInteger(0);
		
		this.database = database; 
		
	}
	
	public void enqueue(Message message) {
		queue.add(message);
		int size = queueSize.incrementAndGet();
		
		if(size >= thresholdCount)
			taskQueue.add(new Object());
	}

	@Override
	public void onTimer() {
		
		if(System.currentTimeMillis() - lastTask >= thresholdTime)
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

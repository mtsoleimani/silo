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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.cassandana.silo.conf.Configuration;
import io.cassandana.silo.conf.Constants;
import io.cassandana.silo.database.DatabaseWorkerPool;
import io.cassandana.silo.scheduler.Scheduler;

public class SimpleDispatcher {
	
	protected Scheduler scheduler;
	
	protected List<Worker> workerPool;
	
	protected AtomicInteger roundRobinIndex;
	
	protected int nThreads;
	
	protected ExecutorService executors;
	protected Configuration conf;
	
	public SimpleDispatcher(Configuration conf) {
		this.conf = conf;
		this.nThreads = conf.threads;
		executors = Executors.newFixedThreadPool(nThreads);
		
		if(conf.hasBulk)
			scheduler = new Scheduler(conf.bulkIntervalSeconds, 1, TimeUnit.SECONDS);
		
		roundRobinIndex = new AtomicInteger(0);
		
		workerPool = new ArrayList<>();
		for(int i=0; i<nThreads; i++) {
			Worker worker = new Worker(
					conf.bulkCount, 
					conf.bulkIntervalSeconds * Constants.SECOND_IN_MILLIS, 
					DatabaseWorkerPool.getInstance(conf).getDatabaseWorker(), conf.hasBulk);
			
			if(conf.hasBulk)
				scheduler.addToSchedulerList(worker);
			
			workerPool.add(worker);
			executors.submit(worker);
		}
	}
	
	
	public void start() {
		
	}
}

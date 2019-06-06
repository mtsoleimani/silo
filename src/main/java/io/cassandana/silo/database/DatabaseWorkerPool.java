/*
 *  Copyright 2018 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package io.cassandana.silo.database;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import io.cassandana.silo.conf.Configuration;

public class DatabaseWorkerPool {
	
	private static DatabaseWorkerPool instance;
	public synchronized static DatabaseWorkerPool getInstance(Configuration conf) {
		if(instance == null)
			instance = new DatabaseWorkerPool(conf);
		return instance;
	}
	
	private int poolSize;
	
	private AtomicInteger index;
	
	private ArrayList<DatabaseOperation> dbPool;
	
	
	private DatabaseWorkerPool(Configuration conf) {
		init(conf);
	}

	private void init(Configuration conf) {
		this.poolSize = conf.threads;
		index = new AtomicInteger(0);
		dbPool = new ArrayList<>();
		
		for(int i=0; i<poolSize; i++) {
			DatabaseOperation dbWorker = DatabaseFactory.get(conf);
			dbWorker.connect();
			this.dbPool.add(dbWorker);
		}
	}
	
	
	public DatabaseOperation getDatabaseWorker() {
		return dbPool.get(index.getAndIncrement() % poolSize);
	}

	

}

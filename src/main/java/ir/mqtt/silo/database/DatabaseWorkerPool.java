package ir.mqtt.silo.database;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import ir.mqtt.silo.conf.SysConfig;

public class DatabaseWorkerPool {
	
	private static DatabaseWorkerPool instance;
	public synchronized static DatabaseWorkerPool getInstance(SysConfig conf) {
		if(instance == null)
			instance = new DatabaseWorkerPool(conf);
		return instance;
	}
	
	private int poolSize;
	
	private AtomicInteger index;
	
	private ArrayList<DatabaseOperation> dbPool;
	
	
	private DatabaseWorkerPool(SysConfig conf) {
		init(conf);
	}

	private void init(SysConfig conf) {
		this.poolSize = conf.threads;
		index = new AtomicInteger(0);
		dbPool = new ArrayList<>();
		
		for(int i=0; i<poolSize; i++) {
			DatabaseOperation dbWorker = DatabaseFactory.get(conf);
			dbWorker.tryConnecting();
			this.dbPool.add(dbWorker);
		}
	}
	
	
	public DatabaseOperation getDatabaseWorker() {
		return dbPool.get(index.getAndIncrement() % poolSize);
	}

	

}

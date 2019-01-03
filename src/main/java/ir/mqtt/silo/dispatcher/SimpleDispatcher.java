/*
 *  Copyright 2018 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package ir.mqtt.silo.dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import ir.mqtt.silo.client.IMqttListener;
import ir.mqtt.silo.conf.SysConfig;
import ir.mqtt.silo.database.DatabaseWorkerPool;
import ir.mqtt.silo.scheduler.Scheduler;

public class SimpleDispatcher implements IMqttListener {
	
	private Scheduler scheduler;
	
	private List<Worker> workerPool;
	
	private AtomicInteger roundRobinIndex;
	
	private int nThreads;
	
	private ExecutorService executors;
	private SysConfig conf;
	
	public SimpleDispatcher(SysConfig conf) {
		this.conf = conf;
		this.nThreads = conf.threads;
		executors = Executors.newFixedThreadPool(nThreads);
		scheduler = new Scheduler(conf.bulkIntervalSeconds, 1, TimeUnit.SECONDS);
		
		roundRobinIndex = new AtomicInteger(0);
		
		workerPool = new ArrayList<>();
		for(int i=0; i<nThreads; i++) {
			Worker worker = new Worker(conf.bulkCount, conf.bulkIntervalSeconds, DatabaseWorkerPool.getInstance(conf).getDatabaseWorker());
			scheduler.addToSchedulerList(worker);
			workerPool.add(worker);
			executors.submit(worker);
		}
	}

	@Override
	public void onConnected() {
		System.err.println("connected to mqtt-broker: " + conf.mqttHost + ":" + conf.mqttPort);
	}

	@Override
	public void onDisconnected() {
		System.err.println("disconnected from mqtt-broker: " + conf.mqttHost + ":" + conf.mqttPort);
	}

	@Override
	public void onMessageArrived(String topic, MqttMessage message) {
		workerPool.get(roundRobinIndex.getAndIncrement() % nThreads).enqueue(new Message(topic, message));
	}

}

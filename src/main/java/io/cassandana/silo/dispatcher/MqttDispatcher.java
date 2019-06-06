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


import io.cassandana.silo.client.IMessageListener;
import io.cassandana.silo.client.MyMqttClient;
import io.cassandana.silo.conf.Configuration;

public class MqttDispatcher extends SimpleDispatcher implements IMessageListener {
	

	public MqttDispatcher(Configuration conf) {
		super(conf);
	}
	
	@Override
	public void start() {
		MyMqttClient mqttClient = MyMqttClient.getInstance(conf.mqttConf);
		mqttClient.setMessageListener(this);
		mqttClient.tryConnecting();
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
	public void onMessageArrived(String topic, String message) {
		workerPool.get(roundRobinIndex.getAndIncrement() % nThreads).enqueue(new Message(topic, message));
	}

}

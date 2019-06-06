/*
 *  Copyright 2018 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package io.cassandana.silo.client;


import java.util.List;

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MyMqttClient implements MqttCallbackExtended  {
	
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	private MqttConf conf;
	
	private MqttAsyncClient mqttClient;
	private MqttConnectOptions mqttConnectOptions;
	
	private PingManager pingManager;
	
	private IMessageListener messageListener;
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	private static MyMqttClient instance;
	public static synchronized MyMqttClient getInstance(MqttConf conf) {
		if(instance == null)
			instance = new MyMqttClient(conf);
		return instance;
	}
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	private MyMqttClient(MqttConf conf) {
		setConf(conf);
		init();
	}
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	private void init() {
		pingManager = new PingManager();
		pingManager.setClientId(conf.getUsername());
		
		mqttConnectOptions = new MqttConnectOptions();
//		mqttConnectOptions.setAutomaticReconnect(true);
		mqttConnectOptions.setCleanSession(false);
		mqttConnectOptions.setConnectionTimeout(conf.getConnectionTimeout());
		mqttConnectOptions.setKeepAliveInterval(conf.getKeepAliveInterval());
		mqttConnectOptions.setMaxInflight(conf.getMaxInflight());
		mqttConnectOptions.setPassword(conf.getPassword().toCharArray());
		mqttConnectOptions.setUserName(conf.getUsername());
	}
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	public void disconnect() {
		if(mqttClient == null)
			return;
		
		if(pingManager != null)
			pingManager.stopPingScheduler();
		
		try {
			mqttClient.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	public MqttConf getConf() {
		return conf;
	}
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	public void setConf(MqttConf conf) {
		this.conf = conf;
	}
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	public synchronized void tryConnecting() {

		if (mqttClient != null && mqttClient.isConnected())
			return;
		
        try {
        	mqttClient = new MqttAsyncClient(conf.getUri(), conf.getClientId(), /*persistence*/null, pingManager);
        	
            mqttClient.setCallback(this);
        	mqttClient.connect(mqttConnectOptions);
        	
//        	connecting.set(false);
        } catch(Exception e) {
        	e.printStackTrace();
        	reconnectionHandle();
        } 
	}
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	private void onConnected() {
		DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
        disconnectedBufferOptions.setBufferEnabled(true);
        disconnectedBufferOptions.setBufferSize(conf.getBufferSize());
        disconnectedBufferOptions.setPersistBuffer(false);
        disconnectedBufferOptions.setDeleteOldestMessages(false);
//        mqttClient.setBufferOpts(disconnectedBufferOptions);
        
		pingManager.startPingScheduler(conf.getKeepAliveInterval());
        
		subscribeToTopics(conf.getTopics());
		
        if(messageListener != null)
        	messageListener.onConnected();
        
        
	}
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	@Override
	public void connectionLost(Throwable cause) {
		reconnectionHandle();
	}
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		
	}
	
	private IMqttMessageListener mqttMessageListener = new IMqttMessageListener() {

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception {
			if (messageListener != null)
				messageListener.onMessageArrived(topic, message.toString());
		}
	};
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		
	}
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	@Override
	public void connectComplete(boolean reconnect, String serverURI) {
		if (!reconnect) 
			onConnected();
	}
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	public void subscribeToTopics(List<String> subscriptionTopics) {
		for(String topic : subscriptionTopics)
			subscribeToTopic(topic);
	}
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
//	public boolean subscribeToAllTopics() {
//		return subscribeToTopic("#");
//	}
	
	public boolean subscribeToTopic(String subscriptionTopic) {
		if(!mqttClient.isConnected())
            return false;
		
        try {
        	mqttClient.subscribe(subscriptionTopic, MqttConf.DEFAULT_QOS, mqttMessageListener);
        	return true;
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	public boolean unsubscribeToTopic(String subscriptionTopic) {
		
		if(!mqttClient.isConnected()) 
			return false;
		
        try {
        	mqttClient.unsubscribe(subscriptionTopic);
        	return true;
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	//-----------------------------------------------------------------------------
	public boolean publishMessage(String publishMessage, String publishTopic){

		if(!mqttClient.isConnected())
			return false;
		
        try {
            MqttMessage message = new MqttMessage();
            message.setPayload(publishMessage.getBytes());
            mqttClient.publish(publishTopic, publishMessage.getBytes(), MqttConf.DEFAULT_QOS, false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
	// -------------------------------------------------------------------------
	// -------------------------------------------------------------------------
	// -------------------------------------------------------------------------
	private void reconnectionHandle(){
//		if(handler == null)
//			handler = new Handler();
//		
//		disconnect();
//		
//		delayIndex++;
//		if(delayIndex >= delayList.length) delayIndex = delayList.length - 1;
//		
//		handler.removeCallbacks(asyncConnectingServer);
//		handler.postDelayed(asyncConnectingServer, delayList[delayIndex]);
	}
	// -------------------------------------------------------------------------
	// -------------------------------------------------------------------------
	// -------------------------------------------------------------------------
	public boolean isConnected() {
		if(mqttClient == null)
			return false;
		return mqttClient.isConnected();
	}
	public IMessageListener getMessageListener() {
		return messageListener;
	}
	public void setMessageListener(IMessageListener mqttListener) {
		this.messageListener = mqttListener;
	}

}

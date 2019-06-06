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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.event.ServerHeartbeatFailedEvent;
import com.mongodb.event.ServerHeartbeatStartedEvent;
import com.mongodb.event.ServerHeartbeatSucceededEvent;
import com.mongodb.event.ServerMonitorListener;

import io.cassandana.silo.conf.Constants;
import io.cassandana.silo.dispatcher.Message;


public class MongodbHelper extends DatabaseHelper implements ServerMonitorListener, DatabaseOperation {

	static {
		Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
	}
	
	protected MongoClient mongoClient;
	protected MongoDatabase database;


	public MongodbHelper(String host, int port,
			String dbUsername, String dbPassword,
			String dbName,
			int dbPoolSize, long dbReconnectSeconds) {

		super(host, port, dbUsername, dbPassword, dbName, dbReconnectSeconds);
		this.dbPoolSize = dbPoolSize;
		this.dbReconnectSeconds = dbReconnectSeconds;
	}

	public MongodbHelper(String host, int port,
			String dbUsername, String dbPassword,
			String dbName) {
		super(host, port, dbUsername, dbPassword, dbName);
	}

	public MongodbHelper(String host, int port, String dbName) {
		super(host, port, dbName);
	}

	public MongodbHelper() {
		super();
	}


	protected int dbPoolSize = Runtime.getRuntime().availableProcessors();
	

	protected Runnable reconnectionRunnable = new Runnable() {

		@Override
		public void run() {
			connect();
		}
	};

	private MongoCollection<Document> siloCollection;
	
	protected IDbmsConnectionStatus mIDbmsConnectionStatus;
	protected void setConnectionStatusListener(IDbmsConnectionStatus listener) {
		this.mIDbmsConnectionStatus = listener;
	}

	public synchronized DatabaseOperation connect() {
		MongoClientOptions clientOptions = new MongoClientOptions.Builder()
			.addServerMonitorListener(this)
			.connectionsPerHost(dbPoolSize)
			.build();

		if(dbUsername != null && dbPassword != null) {
			MongoCredential credential = MongoCredential.createCredential(
					dbUsername, dbName,
					dbPassword.toCharArray());

			mongoClient = new MongoClient(new ServerAddress(host,
					port),
					credential,
					clientOptions);
		} else {
			mongoClient = new MongoClient(new ServerAddress(
					host,
					port),
					clientOptions);
		}

		database = mongoClient.getDatabase(dbName);
		setConnected(true);
		onConnected();
		System.out.println("connected to mongodb server: " + host + ":" + port);
		
		return this;
	}

	
	
	public void onConnected() {
		siloCollection = database.getCollection(Constants.SILO_COLLECTION_NAME);
	}


	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}



	public void shutdown() {
		if(mongoClient != null)
			mongoClient.close();

		if(executor != null)
			executor.shutdown();
	}



	@Override
	public void serverHearbeatStarted(ServerHeartbeatStartedEvent arg0) {

	}


	@Override
	public void serverHeartbeatFailed(ServerHeartbeatFailedEvent arg0) {
		setConnected(false);

		//try to reconnect
		executor.schedule(reconnectionRunnable, dbReconnectSeconds, TimeUnit.SECONDS);
	}


	@Override
	public void serverHeartbeatSucceeded(ServerHeartbeatSucceededEvent arg0) {

	}


	@Override
	public void bulkInsert(List<Message> list) {
		if(list == null || list.size() == 0)
			return;
		
		if(list.size() == 1) {
			Document document = new Document();
			document.put(Constants.TOPIC, list.get(0).topic);
			document.put(Constants.MESSAGE, list.get(0).message);
			document.put(Constants.CREATED, list.get(0).receivedAt);
			siloCollection.insertOne(document);
			return;
		}
		
		List<Document> documents = new ArrayList<>();
		for(Message entry: list) {
			Document document = new Document();
			document.put(Constants.TOPIC, entry.topic);
			document.put(Constants.MESSAGE, entry.message.toString());
			document.put(Constants.CREATED, entry.receivedAt);
			documents.add(document);
		}
		siloCollection.insertMany(documents);
	}

	@Override
	public void insert(Message message) {
		// TODO Auto-generated method stub
		
	}


}

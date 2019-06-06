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



import java.util.List;

import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import io.cassandana.silo.conf.Constants;
import io.cassandana.silo.dispatcher.Message;

import com.datastax.driver.core.Session;



public class CassandraHelper extends DatabaseHelper implements DatabaseOperation {

	static {
		((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("com.datastax").setLevel(Level.ERROR);
		((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger("io.netty").setLevel(Level.ERROR);
	}
	
	
	private Cluster cluster;
    private Session session;
	//-------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------
	public CassandraHelper(String host, int port, String dbUsername, String dbPassword, String dbName,
			long dbReconnectSeconds) {

		super(host, port, dbUsername, dbPassword, dbName, dbReconnectSeconds);
	}

	public CassandraHelper(String host, int port, String dbUsername, String dbPassword, String dbName) {
		super(host, port, dbUsername, dbPassword, dbName);
	}

	public CassandraHelper(String host, int port, String dbName) {
		super(host, port, dbName);
	}

	public CassandraHelper() {
		super();
	}
	//-------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------
	@Override
	public void onConnected() {
		System.out.println("connected to Cassandra server: " + host + ":" + port);
	}

	//-------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------
	@Override
	public void shutdown() {
		
		try {
		if(session != null)
			session.close();
		
		if(cluster != null)
			cluster.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	//-------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------
	@Override
	public DatabaseOperation connect() {
		Builder builder = Cluster.builder().addContactPoint(host);
		builder.withPort(port);
		
		if(dbUsername != null && !dbUsername.isEmpty())
			builder.withCredentials(dbUsername, dbPassword);
		
//		PoolingOptions poolingOptions = new PoolingOptions();
//		poolingOptions.
//		builder.withPoolingOptions();

			
        cluster = builder.build();
        session = cluster.connect(dbName);//keyspace
        onConnected();
        return this;
	}
	//-------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------

	@Override
	public void bulkInsert(List<Message> list) {
		// TODO Auto-generated method stub
		
	}
	//-------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------
	@Override
	public void insert(Message message) {
		String query = "INSERT INTO " + Constants.SILO_COLLECTION_NAME + " (" + Constants.TOPIC
				+ "," + Constants.MESSAGE + "," + Constants.CREATED + " ,ts) VALUES(?,?,?, now())";
		session.executeAsync(query, message.topic, message.message, message.receivedAt);
	}
	//-------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------

	
	
	
	
	
	
	
	
}

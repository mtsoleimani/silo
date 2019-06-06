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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.mysql.cj.jdbc.MysqlDataSource;

import io.cassandana.silo.conf.Constants;
import io.cassandana.silo.dispatcher.Message;

public abstract class AbstractSqlDatabase extends DatabaseHelper implements DatabaseOperation {

	//protected Connection connection = null;
	protected MysqlDataSource dataSource = null;

	public AbstractSqlDatabase(String host, int port, String dbUsername, String dbPassword, String dbName,
			long dbReconnectSeconds) {

		super(host, port, dbUsername, dbPassword, dbName, dbReconnectSeconds);
	}

	public AbstractSqlDatabase(String host, int port, String dbUsername, String dbPassword, String dbName) {
		super(host, port, dbUsername, dbPassword, dbName);
	}

	public AbstractSqlDatabase(String host, int port, String dbName) {
		super(host, port, dbName);
	}

	public AbstractSqlDatabase() {
		super();
	}

	@Override
	public void onConnected() {

	}
	
	public Connection getConnection() throws SQLException {
		return (dataSource != null) ? dataSource.getConnection() : null;
	}
	


	@Override
	public void shutdown() {

	}

	@Override
	public void insert(Message message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bulkInsert(List<Message> list) {
		
		System.err.println(list.size() + "");

		if(list == null || list.size() == 0)
			return;
		
		PreparedStatement statement = null;
		Connection connection = null;
		
		try {
			connection = getConnection();
			statement = connection.prepareStatement("INSERT INTO " + Constants.SILO_COLLECTION_NAME + " (" + Constants.TOPIC
					+ "," + Constants.MESSAGE + "," + Constants.CREATED + ") VALUES(?,?,?)");

			for (Message entry : list) {
				statement.setString(1, entry.topic);
				statement.setString(2, entry.message);
				statement.setLong(3, entry.receivedAt);
				statement.addBatch();
			}

			statement.executeBatch();

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			try {
			
				if(connection != null)
					connection.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}
}

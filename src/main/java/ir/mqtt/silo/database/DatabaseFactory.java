/*
 *  Copyright 2018 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package ir.mqtt.silo.database;

import ir.mqtt.silo.conf.SysConfig;

public class DatabaseFactory {
	

	public static DatabaseOperation get(SysConfig conf) {
		
		
		switch (conf.dbEngine) {
		case MYSQL:
			return new MySqlHelper(conf.dbHost, conf.dbPort, conf.dbUsername, conf.dbPassword, conf.dbName);
			
		case POSTGRES:
			return new PostgresHelper(conf.dbHost, conf.dbPort, conf.dbUsername, conf.dbPassword, conf.dbName);
			
		case MONGODB:
			return new MongodbHelper(conf.dbHost, conf.dbPort, conf.dbUsername, conf.dbPassword, conf.dbName);
			
		case UNKNOWN:
			return null;
		}
		
		return null;
	}
	
}

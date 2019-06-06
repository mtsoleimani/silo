/*
 *  Copyright 2018 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package io.cassandana.silo;

import io.cassandana.silo.conf.Configuration;
import io.cassandana.silo.database.DatabaseWorkerPool;
import io.cassandana.silo.dispatcher.MqttDispatcher;

public class Main {

	public static void main(String[] args) {

		Configuration configuration = null;
		try {
			configuration = Configuration.getInstance();
		} catch (Exception e) {
			System.err.println("ERROR...bad conf file.");
			System.exit(0);
		}
		
		
		DatabaseWorkerPool.getInstance(configuration);
		new MqttDispatcher(configuration).start();
		
	}

}

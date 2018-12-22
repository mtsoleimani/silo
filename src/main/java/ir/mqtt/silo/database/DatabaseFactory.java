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

package ir.mqtt.silo.database;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class DatabaseHelper {

	public DatabaseHelper() {
		executor = new ScheduledThreadPoolExecutor(1);
	}
	
	public DatabaseHelper(String host, int port,
			String dbUsername, String dbPassword,
			String dbName) {
		super();
		this.host = host;
		this.port = port;
		this.dbUsername = dbUsername;
		this.dbPassword = dbPassword;
		this.dbName = dbName;
	}

	
	public DatabaseHelper(String host, int port,
			String dbUsername, String dbPassword,
			String dbName, long dbReconnectSeconds) {
		super();
		this.host = host;
		this.port = port;
		this.dbUsername = dbUsername;
		this.dbPassword = dbPassword;
		this.dbName = dbName;
		this.dbReconnectSeconds = dbReconnectSeconds;
	}

	
	
	public DatabaseHelper(String host, int port, String dbName) {
		super();
		this.host = host;
		this.port = port;
		this.dbName = dbName;
		this.dbUsername = null;
		this.dbPassword = null;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	protected String dbUsername = "";
	protected String dbPassword = "";
	protected String dbName;
	protected String host;
	protected int port;
	protected boolean connected = false;
	protected ScheduledThreadPoolExecutor executor;
	protected long dbReconnectSeconds = 5;
}

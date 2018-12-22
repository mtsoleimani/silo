package ir.mqtt.silo.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class PostgresHelper extends AbstractSqlDatabase {

	protected Connection connection = null;

	public PostgresHelper(String host, int port, String dbUsername, String dbPassword, String dbName,
			long dbReconnectSeconds) {

		super(host, port, dbUsername, dbPassword, dbName, dbReconnectSeconds);
	}

	public PostgresHelper(String host, int port, String dbUsername, String dbPassword, String dbName) {
		super(host, port, dbUsername, dbPassword, dbName);
	}

	public PostgresHelper(String host, int port, String dbName) {
		super(host, port, dbName);
	}

	public PostgresHelper() {
		super();
	}

	public synchronized void tryConnecting() {

		try {
			Class.forName("org.postgresql.Driver");
			this.connection = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + dbName,
					dbUsername, dbPassword);

			setConnected(true);
			onConnected();
			System.out.println("connected to postgres server: " + host + ":" + port);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

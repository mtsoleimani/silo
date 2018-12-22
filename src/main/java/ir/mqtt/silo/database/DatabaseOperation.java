package ir.mqtt.silo.database;

import java.util.List;

import ir.mqtt.silo.dispatcher.Message;

public interface DatabaseOperation {

	
	public void onConnected();

	public void tryConnecting();

	public  void shutdown();

	public boolean isConnected();
	
	public void bulkInsert(List<Message> list);
}

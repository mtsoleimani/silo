package ir.mqtt.silo.database;

public interface IDbmsConnectionStatus {

	public void onConnected();
	public void onDisconnected();
}

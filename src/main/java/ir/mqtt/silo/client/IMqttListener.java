package ir.mqtt.silo.client;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface IMqttListener {
	
	public void onConnected();
	
	public void onDisconnected();
	
	public void onMessageArrived(String topic, MqttMessage message);
	
}

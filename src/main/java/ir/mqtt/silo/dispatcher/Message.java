package ir.mqtt.silo.dispatcher;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Message {

	public Message() {

	}

	public Message(String topic, MqttMessage message) {
		this.topic = topic;
		this.message = message;
	}

	public String topic;

	public MqttMessage message;


	public long receivedAt = System.currentTimeMillis();

}

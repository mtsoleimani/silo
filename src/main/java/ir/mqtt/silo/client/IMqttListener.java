/*
 *  Copyright 2018 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

package ir.mqtt.silo.client;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface IMqttListener {
	
	public void onConnected();
	
	public void onDisconnected();
	
	public void onMessageArrived(String topic, MqttMessage message);
	
}

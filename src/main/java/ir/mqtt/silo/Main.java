package ir.mqtt.silo;

import ir.mqtt.silo.client.MyMqttClient;
import ir.mqtt.silo.conf.SysConfig;
import ir.mqtt.silo.database.DatabaseWorkerPool;
import ir.mqtt.silo.dispatcher.SimpleDispatcher;

public class Main {

	public static void main(String[] args) {

		SysConfig sysConfig = null;
		try {
			sysConfig = SysConfig.getInstance();
		} catch (Exception e) {
			System.err.println("ERROR...bad conf file.");
			System.exit(0);
		}
		
		
		DatabaseWorkerPool.getInstance(sysConfig);
		
		MyMqttClient client = MyMqttClient.getInstance(sysConfig.mqttConf);
		SimpleDispatcher dispatcher = new SimpleDispatcher(sysConfig);
		client.setMqttListener(dispatcher);
		client.tryConnecting();
	}

}

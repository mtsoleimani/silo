package ir.mqtt.silo.client;

public class MqttConf {

	public static final int MQTT_QOS_0 = 0;
	public static final int MQTT_QOS_1 = 1;
	public static final int MQTT_QOS_2 = 2;
	
	public static final int DEFAULT_QOS = MQTT_QOS_2;
	public static final int DEFAULT_BUFFER_SIZE = 500;
	public static final int DEFAULT_MAX_INFLIGHT = 1000;
	public static final int DEAFULT_KEEP_ALIVE_INTERVAL = 300;
	public static final int DEFAULT_CONNECTION_TIMEOUT = 120;
	
	public static final String MQTT_PROTOCOL_TCP = "tcp";
	public static final String MQTT_PROTOCOL_TLS = "tls";
	
	public static final int DEFAULT_PORT = 1883;
	public static final String DEFAULT_PROTOCOL = MQTT_PROTOCOL_TCP;

	
	private String domain;
	private String clientId;
	private String username;
	private String password;
	private String host;
	private int port = DEFAULT_PORT;
	private String protocol = DEFAULT_PROTOCOL;
	private int bufferSize = DEFAULT_BUFFER_SIZE;
	private int maxInflight = DEFAULT_MAX_INFLIGHT;
	private int keepAliveInterval = DEAFULT_KEEP_ALIVE_INTERVAL;
	private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
	
	public String getUri() {
		return getProtocol() + "://" + getHost() + ":" + getPort();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getKeepAliveInterval() {
		return keepAliveInterval;
	}

	public void setKeepAliveInterval(int keepAliveInterval) {
		this.keepAliveInterval = keepAliveInterval;
	}

	public int getMaxInflight() {
		return maxInflight;
	}

	public void setMaxInflight(int maxInflight) {
		this.maxInflight = maxInflight;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}

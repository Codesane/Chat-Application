package network.global.handshake;

import java.io.Serializable;

public class HandshakeRequestEvent implements Serializable {
	
	/** SerialID (Not a necessity since we're in the same workspace) */
	private static final long serialVersionUID = -7337967097347692149L;
	
	private String username, password, version, accessKey;
	
	public HandshakeRequestEvent(String username, String password,
								 String version, String accessKey) {
		this.username = username;
		this.password = password;
		this.version = version;
		this.accessKey = accessKey;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getVersion() {
		return this.version;
	}
	
	public String getAccessKey() {
		return this.accessKey;
	}
	
}

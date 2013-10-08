package network.global.objects;

import java.io.Serializable;

public class NetworkSharedUser implements Serializable {
	private static final long serialVersionUID = -737532310269685341L;
	
	private int userId;
	
	private String username;
	
	public NetworkSharedUser(int id, String name) {
		this.userId = id;
		this.username = name;
	}
	
	public String getName() {
		return this.username;
	}
	
	public int getId() {
		return this.userId;
	}

}

package network.global.update;

import java.io.Serializable;

import network.global.objects.NetworkSharedUser;

public class UserSyncEvent implements Serializable {
	private static final long serialVersionUID = 3446797630036616028L;
	
	private NetworkSharedUser[] users;
	
	public UserSyncEvent(NetworkSharedUser[] users) {
		this.users = users;
	}
	
	public NetworkSharedUser[] getOnlineUsers() {
		return this.users;
	}
	
}

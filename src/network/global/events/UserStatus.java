package network.global.events;

import java.io.Serializable;

import network.global.objects.NetworkSharedUser;

/**
 * Cross network status message,
 * triggered when a user has logged out or in.
 * */
public class UserStatus implements Serializable {
	private static final long serialVersionUID = 4257536338725097134L;
	
	public static final short USER_LOGGED_IN = 0x01;
	public static final short USER_LOGGED_OUT = 0x02;
	
	private short status;
	
	private NetworkSharedUser user;
	
	public UserStatus(short status, NetworkSharedUser user) {
		this.status = status;
		this.user = user;
	}
	
	/** Returs the new state of the user. */
	public short getStatus() {
		return this.status;
	}
	
	/** Returns the user the state concerns */
	public NetworkSharedUser getUser() {
		return this.user;
	}
	
}

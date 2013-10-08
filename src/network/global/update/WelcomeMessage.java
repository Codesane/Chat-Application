package network.global.update;

import java.io.Serializable;

import network.global.objects.NetworkSharedUser;

public class WelcomeMessage implements Serializable{
	private static final long serialVersionUID = -2477612739957928158L;
	
	private String welcomeStr;
	
	private NetworkSharedUser theUser;
	
	public WelcomeMessage(String welcomeMessage, NetworkSharedUser theUser) {
		this.welcomeStr = welcomeMessage;
		this.theUser = theUser;
	}
	
	public NetworkSharedUser getUser() {
		return this.theUser;
	}
	
	public String getWelcomeMessage() {
		return this.welcomeStr;
	}
}

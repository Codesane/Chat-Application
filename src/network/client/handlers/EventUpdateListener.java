package network.client.handlers;

import java.util.ArrayList;

import network.global.objects.NetworkSharedUser;
import network.global.update.WelcomeMessage;

public interface EventUpdateListener {
	
	/** Callback for the WelcomeMessage */
	public void welcomeMessageReceived(WelcomeMessage wm);
	
	public void userSyncUpdate(ArrayList<NetworkSharedUser> users);
	
	public void userLoggedOut(NetworkSharedUser u);
	public void userLoggedIn(NetworkSharedUser u);
	
}

package network.client.handlers;

import network.global.messages.DefaultMessage;

public interface MessageListener {
	
	public void messageReceived(DefaultMessage msg);
	
}

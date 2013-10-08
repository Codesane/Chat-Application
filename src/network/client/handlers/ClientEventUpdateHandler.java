package network.client.handlers;

import network.client.util.Users;
import network.global.events.UserStatus;
import network.global.update.UserSyncEvent;
import network.global.update.WelcomeMessage;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class ClientEventUpdateHandler extends SimpleChannelHandler {
	
	private EventUpdateListener updateListener;
	
	public ClientEventUpdateHandler(EventUpdateListener updateListener) {
		this.updateListener = updateListener;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		
		if(e.getMessage() instanceof WelcomeMessage) {
			updateListener.welcomeMessageReceived((WelcomeMessage) e.getMessage());
		} else if(e.getMessage() instanceof UserSyncEvent) {
			Users.syncUsers((UserSyncEvent) e.getMessage());
		} else if(e.getMessage() instanceof UserStatus) {
			Users.userStatusChanged((UserStatus) e.getMessage());
		}
		
		super.messageReceived(ctx, e);
	}
}

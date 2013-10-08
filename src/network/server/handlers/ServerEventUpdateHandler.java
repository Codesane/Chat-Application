package network.server.handlers;

import network.global.events.Events;
import network.global.events.NetworkEvent;
import network.global.events.UserStatus;
import network.global.update.UpdateHeaderEvent;
import network.global.update.WelcomeMessage;
import network.server.database.DBConstants;
import network.server.database.Database;
import network.server.sessions.Sessions;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class ServerEventUpdateHandler extends SimpleChannelHandler {
	
	private int sessionId = -1;
	
	public ServerEventUpdateHandler(int sessionId) {
		this.sessionId = sessionId;
		Sessions.sendUserUpdateStatusEvent(new UserStatus(UserStatus.USER_LOGGED_IN, 
											Sessions.getUserAsSharedNetworkUser(Sessions.getSession(sessionId).getUser())));
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if(e.getMessage() instanceof UpdateHeaderEvent) {
			if(((UpdateHeaderEvent)(e.getMessage())).getReadyMessage() == 
					UpdateHeaderEvent.WELCOME_MSG_READY) {
				NetworkEvent netEvent = Events.createNewEvent(ctx.getChannel());
				
				netEvent.setMessage(new WelcomeMessage(
										Database.getSettingsOption(DBConstants.SETTINGS_SERVER_MOTD),
										Sessions.getUserAsSharedNetworkUser(Sessions.getSession(sessionId).getUser())));
				netEvent.send();
				
			} else if(((UpdateHeaderEvent)(e.getMessage())).getReadyMessage() ==
					UpdateHeaderEvent.USER_SYNC_READY) {
				// Send the userSyncEvent when the client is ready for it.
				Sessions.sendUserSyncEvent(sessionId);
			}
		}
		super.messageReceived(ctx, e);
	}
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		if(Sessions.getSession(sessionId) != null) {
			Sessions.removeSession(sessionId);
		}
		super.channelDisconnected(ctx, e);
	}
	
}

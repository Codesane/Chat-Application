package network.server.handlers;

import network.global.events.Events;
import network.global.events.NetworkEvent;
import network.global.messages.BroadcastMessage;
import network.global.messages.PrivateMessage;
import network.server.actions.Action;
import network.server.actions.Actions;
import network.server.actions.Actions.ActionType;
import network.server.sessions.Session;
import network.server.sessions.Sessions;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class ServerMessageHandler extends SimpleChannelHandler {
	
	private int userId;
	
	private String username;
	
	public ServerMessageHandler(int userId) {
		this.userId = userId;
		this.username = Sessions.getSession(userId).getUser().getUsername();
		Actions.addAction(new Action(ActionType.INFO, "New message handler instantiated." +
				"\n[ID: 0x" + Integer.toHexString(userId) + "]" +
				"\n[Username: " + username + "]"));
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if(e.getMessage() instanceof PrivateMessage) {
			
			PrivateMessage pm = (PrivateMessage) e.getMessage();
			
			int toId = pm.getToId();
			
			PrivateMessage newPM = new PrivateMessage(userId, username, pm.getMessage());
			Session toSession = Sessions.getSession(toId);
			if(toSession != null) {
				NetworkEvent event = Events.createNewEvent(toSession.getChannel());
				event.setMessage(newPM);
				
				Actions.addAction(new Action(ActionType.INFO, "Redirecting private message;\n" +
						"[0x" + Integer.toHexString(userId) + " => 0x" + Integer.toHexString(pm.getToId()) + "]"));
				
				event.send();
			}
		} else if(e.getMessage() instanceof BroadcastMessage) {
			BroadcastMessage bcmsg = (BroadcastMessage) e.getMessage();
			Actions.addAction(new Action(ActionType.INFO, "Redirecting Broadcast message;\n" +
					"[0x" + Integer.toHexString(userId) + " => ALL"));
			Sessions.sendToAll(userId, new BroadcastMessage(
				userId,
				Sessions.getSession(userId).getUser().getUsername(),
				bcmsg.getMessage()
			));
			
		}
		
		super.messageReceived(ctx, e);
	}
	
}

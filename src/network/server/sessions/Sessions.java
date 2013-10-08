package network.server.sessions;

import java.util.Map;

import network.global.events.Events;
import network.global.events.NetworkEvent;
import network.global.events.NetworkMultipleEvent;
import network.global.events.UserStatus;
import network.global.messages.BroadcastMessage;
import network.global.objects.NetworkSharedUser;
import network.global.update.UserSyncEvent;
import network.server.actions.Action;
import network.server.actions.Actions;
import network.server.actions.Actions.ActionType;
import network.server.app.Server;
import network.server.users.ServerUser;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.ChannelGroupFutureListener;
import org.jboss.netty.util.internal.ConcurrentHashMap;

public final class Sessions {
	
	private static Map<Integer, Session> sessions = new ConcurrentHashMap<Integer, Session>();
	
	/** Adds the session, for convenience sake we use the userID as the session id as well. */
	public static void addSession(Session s) {
		
		// If statement will trigger if a user with this name is already logged in,
		// the user will then be kicked from the server and replaced with this new connection as the same user.
		Session _sess = sessions.get(s.getUser().getId());
		if(_sess != null) {
			// The user id is always the same as the session id.
			Sessions.destroySession(_sess.getUser().getId());
			Server.removeChannel(_sess.getChannel());
		}
		Server.addChannel(s.getChannel());
		sessions.put(s.getUser().getId(), s);
	}
	
	/** Sending an update to all the users online that a new connection(user) has logged in. */
	public static void sendUserUpdateStatusEvent(UserStatus status) {
		Actions.addAction(new Action(ActionType.INFO, "Emitting New User Connection Status Changed Signal to All Connections." +
				"\n[ID: 0x" + Integer.toHexString(status.getUser().getId()) + "]" +
				"\n[Username: " + status.getUser().getName() + "]"));
		NetworkEvent statusUpdateEvent = Events.createNewEvent(Server.getChannelGroup());
		statusUpdateEvent.setMessage(status);
		statusUpdateEvent.send();
	}
	
	/** Sends an user sync event to a single user given the session id. */
	public static synchronized void sendUserSyncEvent(int sessionId) {
		Actions.addAction(new Action(ActionType.INFO, "Sending user synchronization to Session: 0x" + Integer.toHexString(sessionId) + "."));
		NetworkEvent event = Events.createNewEvent(Sessions.getSession(sessionId).getChannel());
		event.setMessage(new UserSyncEvent(getUserArray()));
		event.send();
	}
	
	/** Sends an update to all the clients. */
	public static synchronized void sendUserUpdateEvent() {
		Actions.addAction(new Action(ActionType.INFO, "Sending User Synchronization Event to All Connections."));
		NetworkEvent userUpdateEvent = Events.createNewEvent(Server.getChannelGroup());
		userUpdateEvent.setMessage(new UserSyncEvent(Sessions.getUserArray()));
		userUpdateEvent.send();
	}
	
	/** Removes this id from the sessions. */
	public static void removeSession(int id) {
		ServerUser su = Sessions.getSession(id).getUser();
		Sessions.sendUserUpdateStatusEvent(new UserStatus(UserStatus.USER_LOGGED_OUT, 
				Sessions.getUserAsSharedNetworkUser(su)));
		Server.removeChannel(sessions.get(id).getChannel());
		
		Actions.addAction(new Action(ActionType.INFO, "A session was removed from the Server." +
				"\n[ID: 0x" + Integer.toHexString(su.getId()) + "]" +
				"\n[Username: " + su.getUsername() + "]"));
		
		sessions.remove(id);
	}
	
	/** Just a simplified method. */
	public static synchronized NetworkSharedUser getUserAsSharedNetworkUser(ServerUser su) {
		return new NetworkSharedUser(su.getId(), su.getUsername());
	}
	
	/** Returns all the Sessions user as an array. */
	public static synchronized NetworkSharedUser[] getUserArray() {
		// Global user instance.
		NetworkSharedUser[] users = new NetworkSharedUser[sessions.size()];
		int i = 0;
		for(Session s : sessions.values()) {
			users[i] = new NetworkSharedUser(s.getUser().getId(), s.getUser().getUsername());
			i++;
		}
		return users;
	}
	
	/** Returns the session with the corresponding ID */
	public static Session getSession(int id) {
		return sessions.get(id);
	}
	
	/** Sends a message to everyone on the server,
	 *  the function assumes that the network event supplied has been set with a message.
	 *  @param fId the ID the message was sent from.
	 *  @param bm The BroadcastMessage instance to be sent. */
	public static void sendToAll(int fId, BroadcastMessage bm) {
		Actions.addAction(new Action(ActionType.INFO, "A broadcast message was invoked by session: 0x" + Integer.toHexString(fId) + "."));
		ChannelGroup group = Server.getChannelGroup();
		NetworkEvent event = Events.createNewEvent(group);
		event.setMessage(bm);
		event.send();
	}
	
	/** Kicks the user from the Server as well as letting everyone know the "Server" kicked the user. */
	public static void destroySession(int sessionId) {
		Actions.addAction(new Action(ActionType.INFO, "Attempting to kick session id: 0x" + Integer.toHexString(sessionId) + " from the Server..."));
		Session sess = sessions.get(sessionId);
		if(sess != null) {
			final Channel c = sess.getChannel();
			BroadcastMessage kickMsg = new BroadcastMessage(-1, "SERVER", sess.getUser().getUsername() + 
					" has been kicked by the Server.");
			NetworkMultipleEvent broadcastEvent = Events.createNewEvent((Server.getChannelGroup()));
			
			broadcastEvent.setMessage(kickMsg);
			Sessions.removeSession(sessionId);
			broadcastEvent.setFutureListener(new ChannelGroupFutureListener() {
				@Override
				public void operationComplete(ChannelGroupFuture future) throws Exception {
					if(c != null) {
						c.disconnect();
					}
				}
			});
			broadcastEvent.send();
		}
	}
}

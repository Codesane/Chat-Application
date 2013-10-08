package network.server.sessions;

import network.server.users.ServerUser;

import org.jboss.netty.channel.Channel;

public class Session {
	
	private ServerUser userSession;
	
	private Channel channel;
	
	public Session(ServerUser user, Channel c) {
		this.userSession = user;
		this.channel = c;
	}
	
	public Channel getChannel() {
		return this.channel;
	}
	
	public ServerUser getUser() {
		return this.userSession;
	}
	
}

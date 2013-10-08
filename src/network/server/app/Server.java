package network.server.app;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import network.server.actions.Action;
import network.server.actions.Actions;
import network.server.actions.Actions.ActionType;
import network.server.database.DBConstants;
import network.server.database.Database;
import network.server.util.ServerContext;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

public class Server {
	
	private static ChannelGroup allChannels = new DefaultChannelGroup("ALL-CHANNELS");
	
	private ServerBootstrap bootstrap;
	
	private int portNumber;
	
	public Server() {
		Actions.addAction(new Action(ActionType.INIT, "Creating Server Instance..."));
		ChannelFactory cFactory = new NioServerSocketChannelFactory(
										Executors.newCachedThreadPool(),
										Executors.newCachedThreadPool());
		
		bootstrap = new ServerBootstrap(cFactory);
		
		Actions.addAction(new Action(ActionType.INIT, "Setting Server port..."));
		portNumber = Integer.parseInt(Database.getSettingsOption(DBConstants.SETTINGS_SERVER_PORT));
	}
	
	/** Binds the server to the port defined in the INI file
	 *  and allows the server to accept incoming connections.
	 *   */
	public void startServer() {
		
		Actions.addAction(new Action(ActionType.INIT, "Attaching Handshake handler to the Pipeline..."));
		
		bootstrap.setPipeline(ServerContext.handshakeHandler);
		
		Actions.addAction(new Action(ActionType.INIT, "Setting \"keepAlive\" and \"tcpNoDelay\" options for server..."));
		bootstrap.setOption("keepAlive", true);
		bootstrap.setOption("tcpNoDelay", true);
		
		Actions.addAction(new Action(ActionType.INIT, "BINDING SERVER TO PORT: " + portNumber + "..."));
		Channel acceptor = bootstrap.bind(new InetSocketAddress(portNumber));
		if(!acceptor.isBound()) {
			Actions.addAction(new Action(ActionType.SEVERE, "COULD NOT BIND SERVER PORT!"));
		} else {
			Actions.addAction(new Action(ActionType.INFO, "Server Status is now Online!"));
			Actions.initDone();
		}
		allChannels.add(acceptor);
	}
	
	/** Closes all channels and releases the bootstrap's resources */
	public void stopServer() {
		Actions.addAction(new Action(ActionType.INFO, "Closing all channels..."));
		ChannelGroupFuture future = allChannels.close();
		Actions.addAction(new Action(ActionType.INFO, "Awaiting completion..."));
		if(future.awaitUninterruptibly(10000)) {
			if(!future.isCompleteSuccess()) {
				Actions.addAction(new Action(ActionType.WARNING, "Channels failed to close!"));
				// TODO: Add to actions.
			} else {
				Actions.addAction(new Action(ActionType.INFO, "Success! All channels closed."));
			}
		}
		Actions.addAction(new Action(ActionType.INFO, "Releasing the bootstrap external resources..."));
		bootstrap.releaseExternalResources();
	}
	
	/** Adds a channel to the group (Should only be done when the channel has successfully
	 *  completed the handshake. */
	public static void addChannel(Channel c) {
		Actions.addAction(new Action(ActionType.INFO, "A channel has been added to the Channel group."));
		synchronized(allChannels) {
			allChannels.add(c);
		}
	}
	
	public static void removeChannel(Channel c) {
		Actions.addAction(new Action(ActionType.INFO, "A channel has been removed from the Channel group."));
		synchronized(allChannels) {
			allChannels.remove(c);
		}
	}
	
	public static ChannelGroup getChannelGroup() {
		synchronized(allChannels) {
			return Server.allChannels;
		}
	}
	
}

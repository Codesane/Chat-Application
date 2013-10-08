package network.client.util;

import java.util.ArrayList;

import network.client.handlers.ClientEventUpdateHandler;
import network.client.handlers.ClientHandshakeHandler;
import network.client.handlers.ClientMessageHandler;
import network.client.handlers.EventUpdateListener;
import network.client.handlers.MessageListener;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ConnectTimeoutException;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;


public final class ClientContext {
	
	public static volatile Channel clientChannel;
	
	public static MessageListener messageListener;
	public static EventUpdateListener updateListener;
	
	/** Enums for the status of the connection,
	 *  UNRESOLVED will be visible if the user has no desired
	 *  connection. */
	public enum ServerStatus {
		ONLINE("GREEN"), OFFLINE("RED"), CONNECTING("BLUE"), UNRESOLVED("ORANGE");
		final String color;
		ServerStatus(String c) {
			this.color = c;
		}
		public String getColor() {
			return this.color;
		}
	}
	
	public enum LoginStatus {
		HANDSHAKING("Handshaking..."), 
		ERROR_USERNAME_PASSWORD("Incorrect Username and/or Password."),
		ERROR_VERSION("Incorrect Client Version."),
		ERROR_KEY("This version of the Client is not genuine."),
		BANNED("You are Banished."),
		SUCCESSFUL("<html><a font color='GREEN'>Success!</a></html>");
		
		final String displayText;
		
		LoginStatus(String txt) {
			this.displayText = txt;
		}
		
		public String getDisplayText() {
			return displayText;
		}
	}
	
	/** Sent with the handshake */
	public static final String CLIENT_VERSION = "1.0.0";
	public static final String CLIENT_AUTH_KEY = "ed0f912fe5368e3b90b569d77275d838";
	
	/** The default time that the client will try to connect to the server before
	 *  throwing an {@link ConnectTimeoutException} . */
	public static final int DEFAULT_CONNECTION_TIMEOUT_LIMIT = 5000;
	
	/** Encoder for an object coming downstream (Out) */
	private static final ObjectEncoder objEncoder = new ObjectEncoder();

	/** Decoder for an object coming upstream (In) */
	private static final ObjectDecoder objDecoder = new ObjectDecoder(
			ClassResolvers.weakCachingConcurrentResolver(null));
	
	/** Holds an instance of the handshake handler */
	private static ClientHandshakeHandler handshakeHandler = new ClientHandshakeHandler(); // (This line MUST be above the handshakePipeline
	
	/** Pipeline containing the handlers for a handshake to be performed.
	 *  This one will be switched with the messagePipeline once the handshake
	 *  has completed <b>successfully</b>. */
	public static ChannelPipeline handshakePipeline = createHandshakePipeline();
	
	
	/** Returns the handshake handler. */
	public static ClientHandshakeHandler getHandshakeHandler() {
		return ClientContext.handshakeHandler;
	}
	
	/** Creates a Handshake handler and returns the ChannelPipeline. */
	private static ChannelPipeline createHandshakePipeline() {
		ChannelPipeline pipe = Channels.pipeline(objEncoder, objDecoder);
		pipe.addLast("handshakeHandler", handshakeHandler);
		return pipe;
	}
	
	/** This works because we only have one channelhandler in our pipeline when switching,
	 *  we currently have  */
	public static ArrayList<ChannelHandler> createMessagePipeline() {
		ArrayList<ChannelHandler> handlers = new ArrayList<ChannelHandler>(2);
		handlers.add(new ClientEventUpdateHandler(updateListener)); 
		handlers.add(new ClientMessageHandler(messageListener));
		return handlers;
	}
	
}

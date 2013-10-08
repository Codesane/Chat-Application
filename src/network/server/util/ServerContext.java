package network.server.util;

import java.util.ArrayList;

import network.server.handlers.ServerEventUpdateHandler;
import network.server.handlers.ServerHandshakeHandler;
import network.server.handlers.ServerMessageHandler;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.serialization.ClassResolvers;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

public final class ServerContext {
	
	
	private static final ObjectEncoder objEncoder = new ObjectEncoder();
	private static final ObjectDecoder objDecoder = new ObjectDecoder(
				ClassResolvers.weakCachingConcurrentResolver(null));
	
	
	public static ChannelPipeline handshakeHandler = createHandshakePipeline();
	
	private static ChannelPipeline createHandshakePipeline() {
		ChannelPipeline pipeline = Channels.pipeline(objEncoder, objDecoder);
		pipeline.addLast("handshakeHandler", new ServerHandshakeHandler());
		return pipeline;
	}
	
	public static ArrayList<ChannelHandler> createMasterHandlers(int userId) {
		ArrayList<ChannelHandler> handlers = new ArrayList<ChannelHandler>(2);
		handlers.add(new ServerEventUpdateHandler(userId)); 
		handlers.add(new ServerMessageHandler(userId));
		return handlers;
	}
	
	
}

package network.server.handlers;

import java.util.ArrayList;

import network.global.events.Events;
import network.global.events.NetworkEvent;
import network.global.handshake.HandshakeRequestEvent;
import network.global.handshake.HandshakeResponseEvent.HandshakeStatus;
import network.global.handshake.ValidatedHandshakeResponse;
import network.server.actions.Action;
import network.server.actions.Actions;
import network.server.actions.Actions.ActionType;
import network.server.handshakeutil.HandshakeValidator;
import network.server.util.ServerContext;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ServerHandshakeHandler extends SimpleChannelUpstreamHandler {
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if(e.getMessage() instanceof HandshakeRequestEvent) {
			
			Actions.addAction(new Action(ActionType.INFO, "Received handshake from Connection, validating..."));
			
			/* Create a new NetworkEvent */
			NetworkEvent handshakeEvent = Events.createNewEvent(ctx.getChannel());
			
			/* Validate the handshake and supply that as the message of the event. */
			ValidatedHandshakeResponse response = 
					HandshakeValidator.validate((HandshakeRequestEvent) e.getMessage(), ctx.getChannel());
			handshakeEvent.setMessage(response);
			
			Actions.addAction(new Action(ActionType.INFO, "Handshake validation complete!\n" +
					"[Result => " + response.getStatus().toString() + "]\n" +
					"[Code => " + response.getId() + "]\n"));
			
			/* Send the event to the client to perform actions based on the result. */
			handshakeEvent.send();
			if(response.getStatus() == HandshakeStatus.SUCCESSFUL) {
				Actions.addAction(new Action(ActionType.INFO, "Setting default pipeline for " +
						"session Id: 0x" + Integer.toHexString(response.getId()) + "."));
				ctx.getPipeline().remove(this);
				ArrayList<ChannelHandler> newHandlers = ServerContext.createMasterHandlers(response.getId());
				for(int i = 0; i < newHandlers.size(); i++) {
					ctx.getPipeline().addLast("handler-" + i, newHandlers.get(i));
				}
			}
		}
		super.messageReceived(ctx, e);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		Actions.addAction(new Action(ActionType.WARNING, "An exception occurred in a pipeline." +
				"\nStack: " + e.getCause()));
		super.exceptionCaught(ctx, e);
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		Actions.addAction(new Action(ActionType.INFO, "Received a new Connection."));
		super.channelConnected(ctx, e);
	}
	
}

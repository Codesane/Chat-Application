package network.client.handlers;

import java.util.ArrayList;

import network.client.util.ClientContext;
import network.global.events.Events;
import network.global.events.NetworkEvent;
import network.global.handshake.HandshakeRequestEvent;
import network.global.handshake.HandshakeResponseEvent;
import network.global.handshake.HandshakeResponseEvent.HandshakeStatus;
import network.global.update.UpdateHeaderEvent;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ClientHandshakeHandler extends SimpleChannelUpstreamHandler {
	
	private HandshakeResponseListener listener;
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		if(e.getMessage() instanceof HandshakeResponseEvent) {
			if(listener != null) {
				listener.handshakeResponseReceived((HandshakeResponseEvent) e.getMessage());
			}
			if(((HandshakeResponseEvent) e.getMessage()).getStatus() == HandshakeStatus.SUCCESSFUL) {
				NetworkEvent event = Events.createNewEvent(ClientContext.clientChannel);
				event.setMessage(new UpdateHeaderEvent(UpdateHeaderEvent.WELCOME_MSG_READY));
				event.send();
				
				ArrayList<ChannelHandler> handlers = ClientContext.createMessagePipeline();
				
				for(int i = 0; i < handlers.size(); i++) {
					ctx.getPipeline().addLast("handler-" + i, handlers.get(i));
					
				}
				
				ctx.getPipeline().remove(this);
			}
		}
		super.messageReceived(ctx, e);
	}
	
	public void sendHandshakeRequest(String username, String password) {
		if(ClientContext.clientChannel == null) {
			throw new NullPointerException("Channel null. Client not yet Connected.");
		}
		HandshakeRequestEvent request = 
				new HandshakeRequestEvent(username, password, ClientContext.CLIENT_VERSION, ClientContext.CLIENT_AUTH_KEY);
		NetworkEvent handshakeEvent = Events.createNewEvent(ClientContext.clientChannel);
		handshakeEvent.setMessage(request);
		handshakeEvent.send();
	}
	
	public void setHandshakeResponseListener(HandshakeResponseListener listener) {
		this.listener = listener;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		System.err.println(e.getCause());
		super.exceptionCaught(ctx, e);
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		ClientContext.clientChannel = ctx.getChannel();
		super.channelConnected(ctx, e);
	}
	
	public interface HandshakeResponseListener {
		public void handshakeResponseReceived(HandshakeResponseEvent e);
	}
	
}

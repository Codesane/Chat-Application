package network.client.handlers;

import network.global.messages.DefaultMessage;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

public class ClientMessageHandler extends SimpleChannelHandler {
	
	private MessageListener messageListener;
	
	public ClientMessageHandler(MessageListener ml) {
		this.messageListener = ml;
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		
		if(e.getMessage() instanceof DefaultMessage) {
			messageListener.messageReceived((DefaultMessage) e.getMessage());
		}
		
		super.messageReceived(ctx, e);
	}
}

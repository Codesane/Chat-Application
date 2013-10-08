package network.global.events;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

public class NetworkSingleEvent extends NetworkEvent implements ChannelFutureListener {
	
	/** Channel supplied by the Events.createNewEvent method,
	 *  this channel is used to write the object message to the peer */
	private Channel channel;
	
	/**  */
	private ChannelFuture future;
	
	private ChannelFutureListener futureListener;
	
	protected NetworkSingleEvent(Channel c, int id) {
		super(id);
		this.channel = c;
	}
	
	public void setFutureListener(ChannelFutureListener cfl) {
		this.futureListener = cfl;
	}
	
	@Override
	public void send() {
		if(getMessage() == null) {
			throw new NullPointerException("message");
		} else if(channel == null) {
			throw new NullPointerException("channel");
		} else {
			channel.write(getMessage()).addListener(this);
			setIsSent(true);
		}
	}
	
	@Override
	public void operationComplete(ChannelFuture future) throws Exception {
		NetworkSingleEvent.this.future = future;
		if(futureListener != null) {
			NetworkSingleEvent.this.futureListener.operationComplete(future);
		}
	}
	
	@Override
	public boolean isSuccessful() {
		if(future != null) {
			return this.future.isSuccess();
		} else {
			return false;
		}
	}
	
}

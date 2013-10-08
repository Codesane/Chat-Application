package network.global.events;

import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.ChannelGroupFutureListener;

public class NetworkMultipleEvent extends NetworkEvent implements ChannelGroupFutureListener {
	
	private ChannelGroupFutureListener listener;
	
	private ChannelGroupFuture groupFuture;
	
	private ChannelGroup channelGroup;
	
	protected NetworkMultipleEvent(ChannelGroup cg, int id) {
		super(id);
		this.channelGroup = cg;
	}
	
	/** Sets a listener to the event. */
	public void setFutureListener(ChannelGroupFutureListener cgfl) {
		this.listener = cgfl;
	}
	
	@Override
	public void send() {
		if(getMessage() != null) {
			this.channelGroup.write(getMessage()).addListener(this);
			setIsSent(true);
		} else {
			throw new NullPointerException("message");
		}
	}

	@Override
	public boolean isSuccessful() {
		if(groupFuture != null) {
			return groupFuture.isCompleteSuccess();
		} else {
			return false;
		}
	}

	@Override
	public void operationComplete(ChannelGroupFuture future) throws Exception {
		NetworkMultipleEvent.this.groupFuture = future;
		if(listener != null) {
			listener.operationComplete(future);
		}
	}
	
}

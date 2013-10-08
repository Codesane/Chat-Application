package network.global.events;


import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;


public final class Events {
	
	private static volatile int CURRENT_EVENT_ID = 0;
	
	/** Gets the ID at which the last event created was assigned. */
	public static int getCurrentEventId() {
		return CURRENT_EVENT_ID;
	}
	
	/** Creates and returns a new {@link NetworkSingleEvent}. 
	 *  This NetworkEvent will send the message to a SINGLE user.*/
	public static NetworkSingleEvent createNewEvent(Channel c) {
		return new NetworkSingleEvent(c, CURRENT_EVENT_ID++);
	}
	
	/** Creates and returns a new {@link NetworkMultipleEvent}.
	 *  This NetworkEvent will send the message to all users contained in the
	 *  channel group. <br/><br/> */
	public static NetworkMultipleEvent createNewEvent(ChannelGroup cg) {
		return new NetworkMultipleEvent(cg, CURRENT_EVENT_ID++);
	}
	
}

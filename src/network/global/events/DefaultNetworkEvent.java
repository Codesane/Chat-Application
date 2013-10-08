package network.global.events;


public interface DefaultNetworkEvent {
	
	/** Returns the id of this event, 
	 *  The id is generated and supplied when the 
	 *  Events createNewEvent method is invoked. */
	public int getId();
	
	/** The message to be sent to the server/client */
	public Object getMessage();
	
	/** The time in milliseconds that the package was sent. */
	public long getSystemMillisSent();
	
	/** Sets the message to be sent from the event(Must be a serializable class) */
	public void setMessage(Object msg);
	
	/** Sends the Object to the server */
	public void send();
	
	/** Returns true if and only if the message was sent. (No matter the result) */
	public boolean isSent();
	
	/** Returns true if and only if the ChannelFuture returned Successful.
	 *  <b>NOTE: </b>This flag is set asynchronously.*/
	public boolean isSuccessful();
	
}

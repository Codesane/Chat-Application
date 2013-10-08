package network.global.events;



public abstract class NetworkEvent implements DefaultNetworkEvent {
	private long millisSent = -1L;
	/* TODO: Fix the delay property delta send -> future success.
	private int msDelay = -1;
	*/
	private int eventId;
	private Object message;
	private boolean isSent = false;
	
	
	/** Constructor for the NetworkEvent class,
	 *  can only be called within the same package,
	 *  should be done from the Events.createNewEvent method. */
	protected NetworkEvent(int id) {
		this.eventId = id;
	}
	
	/** Set by the parent of this class. */
	protected void setIsSent(boolean isSent) {
		this.isSent = isSent;
	}
	
	@Override
	public int getId() {
		return this.eventId;
	}

	@Override
	public Object getMessage() {
		return message;
	}
	
	@Override
	public long getSystemMillisSent() {
		return millisSent;
	}

	@Override
	public void setMessage(Object msg) {
		this.message = msg;
	}
	
	@Override
	public boolean isSent() {
		return isSent;
	}
}

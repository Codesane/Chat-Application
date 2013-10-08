package network.global.messages;

public class BroadcastMessage extends DefaultMessage {
	private static final long serialVersionUID = -7547191893784676162L;

	/** Supplied by the client. */
	public BroadcastMessage(String message) {
		super(DefaultMessage.BROADCAST_MESSAGE, message);
	}
	
	/** Supplied by the Server. */
	public BroadcastMessage(int fromId, String sender, String message) {
		super(DefaultMessage.BROADCAST_MESSAGE, fromId, sender, message);
	}
	
}

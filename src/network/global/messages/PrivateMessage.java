package network.global.messages;

public class PrivateMessage extends DefaultMessage {
	private static final long serialVersionUID = -919157040398231504L;
	
	private int toId;
	
	/** Constructor used by the client. */
	public PrivateMessage(int toId, String message) {
		super(DefaultMessage.PRIVATE_MESSAGE, message);
		this.toId = toId;
	}
	
	/** Called by the Server. */
	public PrivateMessage(int fromId, String sender, String message) {
		super(DefaultMessage.PRIVATE_MESSAGE, fromId, sender, message);
	}
	
	/** This will be set by the client, which means that when
	 *  a client receives this message this will be HIS id. */
	public int getToId() {
		return this.toId;
	}
	
}

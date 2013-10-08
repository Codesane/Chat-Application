package network.global.messages;

import java.io.Serializable;

public abstract class DefaultMessage implements DefaultMessageImpl, Serializable {

	private static final long serialVersionUID = -1081137469968477221L;

	public static final short BROADCAST_MESSAGE = 0x00;
	
	public static final short PRIVATE_MESSAGE = 0x01;
	
	private String sender;
	
	private String message;
	
	private short type;
	
	private int fromId;
	
	/** The from ID is set by the server when passing by, this is so that
	 *  the client can't try to confuse himself with someone else. */
	protected DefaultMessage(short type, String message) {
		this(type, -1, null, message);
	}
	
	protected DefaultMessage(short type, int fromId, String sender, String message) {
		this.fromId = fromId;
		this.type = type;
		this.sender = sender;
		this.message = message;
	}
	
	@Override
	public int getFromId() {
		return this.fromId;
	}
	
	@Override
	public short getType() {
		return this.type;
	}

	@Override
	public String getSender() {
		return this.sender;
	}

	@Override
	public String getMessage() {
		return this.message;
	}
	
}

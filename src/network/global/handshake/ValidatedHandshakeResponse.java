package network.global.handshake;

import java.io.Serializable;

public class ValidatedHandshakeResponse implements Serializable, HandshakeResponseEvent {
	private static final long serialVersionUID = 6961432537463878397L;
	
	private int userId = -1;
	
	private HandshakeStatus status;
	
	private short cause;
	
	/**  */
	public ValidatedHandshakeResponse(HandshakeStatus status, int id, short cause) {
		this.status = status;
		this.userId = id;
		this.cause = cause;
	}
	
	/** Constructor takes the cause of the failure in it's parameters */
	public ValidatedHandshakeResponse(short cause) {
		this(HandshakeStatus.FAILED, -1, cause);
	}
	
	/** Constructor takes the id of a successful handshake in it's parameters */
	public ValidatedHandshakeResponse(int id) {
		this(HandshakeStatus.SUCCESSFUL, id, NO_ERROR);
	}
	
	@Override
	public HandshakeStatus getStatus() {
		return this.status;
	}
	
	@Override
	public short getCause() {
		return this.cause;
	}

	@Override
	public int getId() {
		return userId;
	}
}

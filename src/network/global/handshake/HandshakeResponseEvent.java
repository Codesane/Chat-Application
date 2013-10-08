package network.global.handshake;

public interface HandshakeResponseEvent {
	
	public static final short NO_ERROR = 0x00;
	/* The causes for the handshake to fail */
	public static final short CAUSE_USERNAME_PASSWORD_NOMATCH = 0x01;
	public static final short CAUSE_INCORRECT_VERSION = 0x02;
	public static final short CAUSE_INCORRECT_KEY = 0x03;
	public static final short CAUSE_USER_BANNED = 0x04;
	//////////////////////////////////////////
	
	public static enum HandshakeStatus {
		SUCCESSFUL, FAILED;
	}
	
	/** Returns the status of the handshake event. */
	public HandshakeStatus getStatus();
	
	/** Will be null if the handshake succeeded.
	 *  If not, it will contain a message for the user to be displayed
	 *  containing the reason for why the handshake failed. */
	public short getCause();
	
	/** Will be -1 if the handshake fails.
	 *  If not, it will contain the corresponding user-session-id. */
	public int getId();
	
}

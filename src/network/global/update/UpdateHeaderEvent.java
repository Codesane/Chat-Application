package network.global.update;

import java.io.Serializable;

public class UpdateHeaderEvent implements Serializable {
	private static final long serialVersionUID = -2474735761014418254L;
	
	public static final short WELCOME_MSG_READY = 0x01;
	public static final short USER_SYNC_READY = 0x02;
	
	private short readyMessage;
	
	public UpdateHeaderEvent(short rdyMsg) {
		this.readyMessage = rdyMsg;
	}
	
	public short getReadyMessage() {
		return this.readyMessage;
	}
	
}

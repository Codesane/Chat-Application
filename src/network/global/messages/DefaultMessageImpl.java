package network.global.messages;

public interface DefaultMessageImpl {
	
	public int getFromId();
	
	public String getSender();
	
	public String getMessage();
	
	public short getType();
}

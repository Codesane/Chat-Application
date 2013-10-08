package network.server.users;

public interface ServerUserInterface {
	
	/** Returns the user ID */
	public int getId();
	
	/** Returns the time in milliseconds when the user logged in to the server. (This session) */
	public long getTimeLoggedInMillis();
	
	/** Returns the username. */
	public String getUsername();
	
	/** Returns the time in milliseconds that the user registered on the website. */
	public long getTimeRegisteredMillis();
	
	public boolean getIsBanned();
	
	public void setTimeLoggedInMillis(long millis);
}

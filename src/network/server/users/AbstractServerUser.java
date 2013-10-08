package network.server.users;

public abstract class AbstractServerUser implements ServerUserInterface {
	
	private int userId;
	private boolean isBanned;
	private String username;
	private long timeLoggedIn;
	private long timeRegistered;
	
	public AbstractServerUser(int id, String username, long timeRegistered, boolean isBanned) {
		this.userId = id;
		this.username = username;
		this.timeRegistered = timeRegistered;
		this.isBanned = isBanned;
	}
	
	@Override
	public boolean getIsBanned() {
		return isBanned;
	}
	
	@Override
	public int getId() {
		return userId;
	}
	@Override
	public long getTimeLoggedInMillis() {
		return timeLoggedIn;
	}
	@Override
	public String getUsername() {
		return username;
	}
	@Override
	public long getTimeRegisteredMillis() {
		return timeRegistered;
	}
	
	@Override
	public void setTimeLoggedInMillis(long millis) {
		this.timeLoggedIn = millis;
	}

}

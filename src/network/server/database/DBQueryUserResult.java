package network.server.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import network.server.users.AbstractServerUser;
import network.server.users.ServerUser;


public class DBQueryUserResult extends AbstractServerUser {
	
	private DBQueryUserResult(int userId, String username, long timeRegisteredMillis, boolean banned) {
		super(userId, username, timeRegisteredMillis, banned);
	}
	
	public DBQueryUserResult(ResultSet res) throws SQLException {
		this(res.getInt(DBConstants.USER_USER_ID),
			 res.getString(DBConstants.USER_USERNAME),
			 res.getLong(DBConstants.USER_TIME_REGISTERED), 
			 res.getBoolean(DBConstants.USER_BANNED));
	}
	
	/** Sets a login time to now.
	 *  Returns a User class with the login time set to Now. */
	public ServerUser getAsUser() {
		ServerUser u = new ServerUser(getId(), getUsername(), getTimeRegisteredMillis(), getIsBanned());
		return u;
	}
	
}

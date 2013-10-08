package network.server.database;

public final class DBConstants {
	
	/* These values will be used if no parameters were given in the "connect()" method. */
	protected static final String DEFAULT_HOST = "192.168.1.15";
	protected static final String DEFAULT_PORT = "3306";
	protected static final String DEFAULT_USERNAME = "chat-server";
	protected static final String DEFAULT_PASSWORD = "7612bc389aef96be707f562c88d25890";
	protected static final String DEFAULT_DATABASE = "chatapplication";
	
	
	/* Everything that has to do with the Settings table */
	protected static final String SETTINGS_STATEMENT = "SELECT * FROM server_settings";
	public static final String SETTINGS_SERVER_NAME = "server_name";
	public static final String SETTINGS_SERVER_MOTD = "server_motd";
	public static final String SETTINGS_SERVER_PORT = "server_port";
	public static final String SETTINGS_SERVER_VERSION = "server_version";
	public static final String SETTINGS_SERVER_AUTH_KEY = "server_auth_key";
	
	
	/* Final modifiers to access the User properties (Makes it easy to change if neccessary) 
	 * finals with the protected modifier is not needed outside of the Database package (such as password, table etc)*/
	protected static final String USER_TABLE = "users";
	public static final String USER_USER_ID = "user_id";
	public static final String USER_USERNAME = "username";
	protected static final String USER_PASSWORD = "password";
	public static final String USER_TIME_REGISTERED = "timeRegisteredMillis";
	public static final String USER_BANNED = "banned";
	
}

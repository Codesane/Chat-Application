package network.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import network.server.actions.Action;
import network.server.actions.Actions;
import network.server.actions.Actions.ActionType;

public final class Database {
	
	private static Connection dbConnection;
	
	private static Database dbInstance = new Database();
	
	private static Hashtable<String, String> settings;
	
	private Database() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** Returns an instance of this database. */
	public static Database getInstance() {
		return dbInstance;
	}
	
	/** Fills the hashtable with options,
	 *  Locks the dbConnection class while using it. */
	private static void loadSettings() throws SQLException {
		Actions.addAction(new Action(ActionType.INIT, "Loading Database Settings..."));
		if(dbConnection == null) {
			Actions.addAction(new Action(ActionType.SEVERE, "Failed to init database!"));
		}
		synchronized(dbConnection) {
			Statement st = dbConnection.createStatement();
			
			Actions.addAction(new Action(ActionType.INIT, "Preparing and Executing Query..."));
			ResultSet res = st.executeQuery(DBConstants.SETTINGS_STATEMENT);
			
			res.next();
			/*
			 *  Set the size to that of the different options returned 
			 *  (So that the hashtable doesn't allocate unneccessary data) 
			 */
			settings = new Hashtable<String, String>(5);
			Actions.addAction(new Action(ActionType.INFO, "Loading Settings with fixed table size: 5"));
			
			settings.put(DBConstants.SETTINGS_SERVER_NAME, res.getString(DBConstants.SETTINGS_SERVER_NAME));
			
			Actions.addAction(new Action(ActionType.INIT, "Fetching Server Message of The Day..."));
			settings.put(DBConstants.SETTINGS_SERVER_MOTD, res.getString(DBConstants.SETTINGS_SERVER_MOTD));
			
			Actions.addAction(new Action(ActionType.INIT, "Fetching Server Port..."));
			settings.put(DBConstants.SETTINGS_SERVER_PORT, res.getString(DBConstants.SETTINGS_SERVER_PORT));
			
			Actions.addAction(new Action(ActionType.INIT, "Fetching Server Version..."));
			settings.put(DBConstants.SETTINGS_SERVER_VERSION, res.getString(DBConstants.SETTINGS_SERVER_VERSION));
			
			Actions.addAction(new Action(ActionType.INIT, "Fetching Server Authentication Key..."));
			settings.put(DBConstants.SETTINGS_SERVER_AUTH_KEY, res.getString(DBConstants.SETTINGS_SERVER_AUTH_KEY));
			
		}
	}
	
	/** Returns the settings from the database (If the settings isn't loaded
	 *  this method will call the private method "loadSettings" before returning. */
	public static synchronized String getSettingsOption(String opt) {
		Actions.addAction(new Action(ActionType.LOG, "Setting Requested: " + opt + "."));
		
		if(settings == null) {
			try {
				loadSettings();
			} catch(SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return settings.get(opt);
	}
	
	/** Connects to the database given the information in the parameters */
	public static void connect(String dbHost, String dbPort, 
			String dbUsername, String dbPassword, String dbName) throws SQLException {
		if(dbConnection == null) {
			dbConnection = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/"
					+ dbName, dbUsername, dbPassword);
		}
	}
	
	/** Queries the database for the user,
	 *  will return null if the user was not found in the database. */
	public static DBQueryUserResult getUser(String username, String password) {
		Actions.addAction(new Action(ActionType.INFO, "User query requested for input: " + username + "."));
		DBQueryUserResult userQueryResult = null;
		if(username.contains("'") || username.contains("\"") || username.contains(" ")) {
			return null;
		}
		synchronized(dbConnection) {
			try {
				Statement st = dbConnection.createStatement();
				ResultSet res = st.executeQuery(
						"SELECT * FROM " + DBConstants.USER_TABLE +
						" WHERE " + DBConstants.USER_USERNAME + "='" + username + "'" +
						" AND " + DBConstants.USER_PASSWORD + " LIKE BINARY '" + password + "' LIMIT 1");
				/* Return null if the user was not found in the database */
				if(!res.next()) {
					Actions.addAction(new Action(ActionType.INFO, "User Query returned 0x00."));
					return null;
				}
				userQueryResult = new DBQueryUserResult(res);
				Actions.addAction(new Action(ActionType.INFO, "Query Successful! Returning User: " + userQueryResult.getUsername() + "."));
			} catch (SQLException e) {
				Actions.addAction(new Action(ActionType.ERROR, "User Query had an error. Possibility of MySQL Injection Attempt. " + e.toString()));
			}
		}
		return userQueryResult;
	}
	
	/** Connects to the database given the default connection parameters. */
	public static void connect() throws SQLException {
		connect(DBConstants.DEFAULT_HOST, DBConstants.DEFAULT_PORT, DBConstants.DEFAULT_USERNAME,
				DBConstants.DEFAULT_PASSWORD, DBConstants.DEFAULT_DATABASE);
	}
	
	/** Closes the connection,
	 * @throws SQLException: Will be thrown if the disconnect failed. */
	public static void disconnect() throws SQLException {
		synchronized(dbConnection) {
			if(dbConnection != null)
				dbConnection.close();
			else
				throw new NullPointerException("Database");
		}
	}
}

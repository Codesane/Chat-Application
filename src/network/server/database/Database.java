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
		preQueryConnect();
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
			postQueryCleanup(dbConnection, st, res);
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
	
	/** Opens a connection, asks for the user, closes the connection and returns a result.
	 * 
	 *  @param username The username the user has supplied.
	 *  @param password The password the user has supplied.
	 *  <p>The values will be checked before the query, meaning they do not need
	 *  to be SQL safe when passed to this function.
	 *  @return An instance of the DBQueryUserResult if the user was found, <b>null</b>
	 *  otherwise. </p> */
	public static DBQueryUserResult getUser(String username, String password) {
		preQueryConnect();
		Actions.addAction(new Action(ActionType.INFO, "User query requested for input: " + username + "."));
		DBQueryUserResult userQueryResult = null;
		if(username.contains("'") || username.contains("\"") || username.contains(" ")) {
			return null;
		}
		synchronized(dbConnection) {
			Statement st = null;
			ResultSet res = null;
			try {
				st = dbConnection.createStatement();
				res = st.executeQuery(
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
			} finally {
				postQueryCleanup(dbConnection, st, res);
			}
		}
		return userQueryResult;
	}
	
	/**
	 * Will connect to the Database using the default information
	 * contained within the {@link DBConstants} class.
	 * */
	private static void preQueryConnect() {
		try {
			connect();
		} catch(SQLException e) {
			// Exception notified in the "connect(String, String, String, String, String)" method.
		}
	}
	
	/** Connects to the database given the default connection parameters. */
	public static void connect() throws SQLException {
		connect(DBConstants.DEFAULT_HOST, DBConstants.DEFAULT_PORT, DBConstants.DEFAULT_USERNAME,
				DBConstants.DEFAULT_PASSWORD, DBConstants.DEFAULT_DATABASE);
	}
	
	/** Connects to a MySQL Database using the parameters as arguments.
	 * <p>Take a look at {@link DBConstants} for more information.</p>
	 *  <br />
	 *  @param dbHost MySQL Database Hostname.
	 *  @param dbPort MySQL Database Port (Usually 3306)
	 *  @param dbUsername MySQL Username for this session (Make sure the user has the required permissions).
	 *  @param dbPassword MySQL Password for the user defined in the previous argument.
	 *  @param dbName MySQL Database to which the server will query. */
	public static void connect(String dbHost, String dbPort, 
			String dbUsername, String dbPassword, String dbName) throws SQLException {
		if(dbConnection == null || dbConnection.isClosed()) {
			dbConnection = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":" + dbPort + "/"
					+ dbName, dbUsername, dbPassword);
		}
		if(dbConnection == null || dbConnection.isClosed()) {
			Actions.addAction(new Action(ActionType.SEVERE, "Failed to connect to Database."));
		}
	}
	
	/** Called after a query is performed, its job is to close the connections
	 *  to free any resources that are no longer used. <p>
	 *  As documented, every connection should be opened and then closed
	 *  for every query performed.</p>
	 *  
	 *  @param c The global {@link Connection} variable.
	 *  @param st The {@link Statement} containing the query.
	 *  @param rs The {@link ResultSet} associated with the query. */
	private static void postQueryCleanup(Connection c, Statement st, ResultSet rs) {
		try {
			if(rs != null) {
				rs.close();
				st.close();
				c.close();
			}
		} catch(Exception e) {
			Actions.addAction(new Action(ActionType.WARNING, "Unable to close Database connections " +
					"post query."));
		}
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

package network.server.app;

import java.sql.SQLException;

import network.server.actions.Action;
import network.server.actions.Actions;
import network.server.actions.Actions.ActionType;
import network.server.database.Database;

public class ServerLauncher {
	
	private Server server;
	
	public ServerLauncher() {
		Actions.initStart();
		Actions.addAction(new Action(ActionType.INFO, "Starting Server Initialization"));
		try {
			Actions.addAction(new Action(ActionType.INIT, "Connecting to database..."));
			Database.connect();
		} catch(Exception e) {
			Actions.addAction(new Action(ActionType.SEVERE, e.toString()));
		}
		server = new Server();
		startServer();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				server.stopServer();
			}
		}));
	}
	
	/** Starts the server, begins to accept connections. */
	public void startServer() {
		server.startServer();
	}
	
	/** Closes all connected channels and releases the bootstrap.
	 *  Disconnects the MySQL Database link. */
	public void closeServer() {
		server.stopServer();
		try {
			Actions.addAction(new Action(ActionType.INFO, "Disconnecting from Database..."));
			Database.disconnect();
		} catch (SQLException e) {
			Actions.addAction(new Action(ActionType.WARNING, "Could not disconnect from the Database!" + e.toString()));
		}
	}
	
	public static void main(String[] args) {
		Actions.setListener(new ServerConsoleOutputter());
		new ServerLauncher();
	}
}

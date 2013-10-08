package network.client.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;

/**
 * Keeps track of the recent connections the user connected to.
 * This includes any previous sessions if the saveData function is called upon exiting.
 * */
public final class RecentConnections {
	
	private static ArrayList<InetSocketAddress> clientRecent;
	private static boolean isInit = false;
	
	private static final String RESOURCE_LOCATION = "/network/client/util/resources/recent_connections.bin";
	
	private RecentConnections() {
		loadData();
	}
	
	/** Will create a new instance of the RecentConnections object if it has not already
	 *  been allocated. Also loads the file if it is present, otherwise it will attempt to create it. */
	public static void init() {
		if(!isInit) {
			new RecentConnections();
			isInit = true;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void loadData() {
		URL fileLocation = RecentConnections.class.getResource(RESOURCE_LOCATION);
		try {
			if(fileLocation == null) throw new IOException("filepath null --handle");
			ObjectInputStream objectInput = new ObjectInputStream(new FileInputStream(new File(fileLocation.getFile())));
			clientRecent = (ArrayList<InetSocketAddress>) objectInput.readObject();
			objectInput.close();
		} catch (ClassNotFoundException | IOException e) {
			/*
			 * Probably the first run, calling the saveData function to refresh the status. 
			 * */
			e.printStackTrace();
			saveData();
		}
	}
	
	/** Returns an arraylist containing the recent addresses. */
	public static ArrayList<InetSocketAddress> getClientRecent() {
		if(!isInit) { init(); } 
		return RecentConnections.clientRecent;
	}
	
	/** Writes all the data to a file, file location is defined at the top of this class. */
	public static void saveData() {
		try {
			ObjectOutputStream objectOut = new ObjectOutputStream(new FileOutputStream(
					new File(RecentConnections.class.getResource(RESOURCE_LOCATION).getFile())));
			if(clientRecent == null) {
				clientRecent = new ArrayList<InetSocketAddress>();
			}
			objectOut.writeObject(clientRecent);
			objectOut.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/** @return the most recently added connection, null if no entries reside in the array. */
	public static boolean hasRecentConnections() {
		if(clientRecent == null || clientRecent.size() == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	public static InetSocketAddress getMostRecent() {
		if(clientRecent != null) {
			if(clientRecent.size() > 0) {
				return clientRecent.get(clientRecent.size() - 1);
			}
		}
		return null;
	}
	
	/**
	 * Adds a new entry to the recent connections, if the saveData method is called,
	 * this information will be saved to a resource file and accessed when ran at other
	 * times. Will add the new connection to the list if not already present, also returns true if added. */
	public static boolean addNewConnection(InetSocketAddress sockaddr) {
		if(!isInit) { init(); }
		for(InetSocketAddress iSockAddr : clientRecent) {
			if(iSockAddr.equals(sockaddr)) {
				return false;
			}
		}
		RecentConnections.clientRecent.add(sockaddr);
		return true;
	}
	
	public static void removeConnection(InetSocketAddress sockaddr) {
		if(!isInit) { init(); }
		for(int i = 0; i < clientRecent.size(); i++) {
			if(clientRecent.get(i).equals(sockaddr)) {
				clientRecent.remove(i);
			}
		}
	}
}

package network.client.util;

import java.util.ArrayList;

import network.client.handlers.EventUpdateListener;
import network.global.objects.NetworkSharedUser;
import network.global.update.UserStatus;
import network.global.update.UserSyncEvent;

public final class Users {
	
	private static ArrayList<NetworkSharedUser> usersOnline = new ArrayList<NetworkSharedUser>();
	
	private static EventUpdateListener updateListener;
	
	/** Returns an user given the ID */
	public static synchronized NetworkSharedUser getUserById(int id) {
		for(NetworkSharedUser u : usersOnline) {
			if(u.getId() == id) {
				return u;
			}
		}
		return null;
	}
	
	public static synchronized void setEventUpdateListener(EventUpdateListener listener) {
		Users.updateListener = listener;
	}
	
	
	public static synchronized void userStatusChanged(UserStatus status) {
		if(status.getStatus() == UserStatus.USER_LOGGED_IN) {
			usersOnline.add(status.getUser());
			if(updateListener != null) {
				updateListener.userLoggedIn(status.getUser());
			}
		} else if(status.getStatus() == UserStatus.USER_LOGGED_OUT) {
			if(updateListener != null) {
				updateListener.userLoggedOut(status.getUser());
			}
			removeUser(status.getUser().getId());
		}
	}
	
	public static synchronized void addUser(NetworkSharedUser newUser) {
		usersOnline.add(newUser);
		
	}
	/** Returns an user given the username */
	public static synchronized NetworkSharedUser getUserByName(String name) {
		for(NetworkSharedUser u : usersOnline) {
			if(u.getName().equalsIgnoreCase(name)) {
				return u;
			}
		}
		return null;
	}
	
	/** Returns the users online list */
	public static synchronized ArrayList<NetworkSharedUser> getUsersOnline() {
		return usersOnline;
	}
	
	/** Removes an user given the id (By command of the server) */
	public static synchronized void removeUser(int id) {
		for(int i = 0; i < usersOnline.size(); i++) {
			if(usersOnline.get(i).getId() == id) {
				usersOnline.remove(i);
				return;
			}
		}
	}
	
	/** Called once the Client receives the welcome message
	 *  updates the list with online users to display it for the user.
	 *  Also clears the current users online list, use only if a sync is needed,
	 *  otherwise use user status changes to update the user list. */
	public static synchronized void syncUsers(UserSyncEvent sync) {
		usersOnline.clear();
		for(NetworkSharedUser userSync : sync.getOnlineUsers()) {
			usersOnline.add(userSync);
		}
		if(updateListener != null) {
			updateListener.userSyncUpdate(usersOnline);
		}
	}
	
	public interface UsersUpdateListener {
		public void usersUpdated(ArrayList<NetworkSharedUser> users);
	}	
}

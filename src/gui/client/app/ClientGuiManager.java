package gui.client.app;

import gui.client.app.LoginWindow.LoginSuccessfulCallback;

import java.util.ArrayList;

import network.client.app.HostConnectionCallback;
import network.client.handlers.EventUpdateListener;
import network.client.handlers.MessageListener;
import network.global.handshake.HandshakeResponseEvent;
import network.global.messages.BroadcastMessage;
import network.global.messages.DefaultMessage;
import network.global.messages.PrivateMessage;
import network.global.objects.NetworkSharedUser;
import network.global.update.WelcomeMessage;

public class ClientGuiManager implements LoginSuccessfulCallback, EventUpdateListener, MessageListener {
	
	private LoginWindow loginWindow;
	
	private boolean loginIsDisposed = false;
	
	private ClientWindow clientWindow;
	
	public ClientGuiManager(HostConnectionCallback hostConnectionCallback) {
		loginWindow = new LoginWindow(this, hostConnectionCallback);
		clientWindow = new ClientWindow();
	}
	
	/** Displays the login window if it haven't been disposed already. */
	public void displayLoginWindow() {
		if(!loginIsDisposed) {
			loginWindow.displayWindow();
		}
	}
	
	/** Destroys the login window, should only be called from the
	 *  LoginCallback function. */
	private void destroyLoginWindow() {
		loginWindow.destroy();
		loginWindow = null;
		this.loginIsDisposed = true;
	}
	
	/** Returns an instance of the login window. */
	public LoginWindow getLoginWindow() {
		return this.loginWindow;
	}
	
	@Override
	public void loginSuccessful(HandshakeResponseEvent e) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// Preload the client window.
					Thread.sleep(750);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				destroyLoginWindow();
				showClientMainWindow();
			}
		}).start();
	}
	
	/** Displays the main Client window. */
	private void showClientMainWindow() {
		clientWindow.displayWindow();
	}
	
	@Override
	public void welcomeMessageReceived(WelcomeMessage wm) {
		clientWindow.onWelcomeMessageReceived(wm);
	}

	@Override
	public void messageReceived(DefaultMessage msg) {
		if(msg instanceof PrivateMessage) {
			clientWindow.onPrivateMessageReceived((PrivateMessage) msg);
		} else if(msg instanceof BroadcastMessage) {
			clientWindow.onBroadcastMessageReceived((BroadcastMessage) msg);
		}
	}

	@Override
	public void userSyncUpdate(ArrayList<NetworkSharedUser> users) {
		clientWindow.syncUsers(users);
	}

	@Override
	public void userLoggedOut(NetworkSharedUser u) {
		clientWindow.onUserLogout(u);
	}

	@Override
	public void userLoggedIn(NetworkSharedUser u) {
		clientWindow.onUserLogin(u);
	}
	
}

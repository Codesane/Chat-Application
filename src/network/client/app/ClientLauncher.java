package network.client.app;

import exceptions.ServerOfflineException;
import gui.client.app.ClientGuiManager;

import javax.swing.UIManager;

import network.client.util.ClientContext;
import network.client.util.RecentConnections;
import network.client.util.ClientContext.ServerStatus;
import network.client.util.Users;

import org.jboss.netty.channel.ConnectTimeoutException;

public class ClientLauncher implements HostConnectionCallback {
	
	private volatile Client client;
	
	private ClientGuiManager gui;
	
	public ClientLauncher() {
		
		client = new Client();
		
		gui = new ClientGuiManager(this);
		ClientContext.updateListener = gui;
		Users.setEventUpdateListener(gui);
		ClientContext.messageListener = gui;
		gui.displayLoginWindow();
		gui.getLoginWindow().setServerStatusInfo(ServerStatus.UNRESOLVED);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				client.shutdown();
				RecentConnections.saveData();
			}
		});
	}
	
	@Override
	public void connectToHost(final String hostname, final int port) {
		if(client != null) {
			// This will have to do until I find a more final solution...
			client.shutdown();
			client = new Client();
		}
		new Thread(new Runnable() {
			public void run() {
				try {
					gui.getLoginWindow().setServerStatusInfo(ServerStatus.CONNECTING);
					client.connect(hostname, port);
				} catch (ServerOfflineException soeMsg) {
					soeMsg.printStackTrace();
				} catch (ConnectTimeoutException cteMsg) {
					cteMsg.printStackTrace();
				}
				if(client.isConnected()) {
					gui.getLoginWindow().setServerStatusInfo(ServerStatus.ONLINE);
				} else {
					gui.getLoginWindow().setServerStatusInfo(ServerStatus.OFFLINE);
				}
			}
		}).start();
	}
	
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		new ClientLauncher();
	}
}

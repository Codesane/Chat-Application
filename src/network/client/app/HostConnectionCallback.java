package network.client.app;

public interface HostConnectionCallback {
	/** Allows the user to connect via the gui to a custom server. */
	public void connectToHost(String hostname, int port);
}

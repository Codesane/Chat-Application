package network.client.app;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import network.client.util.ClientContext;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ConnectTimeoutException;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import exceptions.ServerOfflineException;


public class Client {
	
	/** Used to connect and to add/remove pipeline handlers
	 *  from the I/O stream. */
	private ClientBootstrap bootstrap;
	private boolean isConnected = false;
	
	public Client() {
		ChannelFactory cFactory = new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(),
				Executors.newCachedThreadPool());

		bootstrap = new ClientBootstrap(cFactory);
	}
	
	public void connect(String host, int port, int timeout) 
			throws ConnectTimeoutException, ServerOfflineException {

		/*
		 * Add this by default to the pipeline, the first thing the user has
		 * to do is to connect and login.
		 */
		bootstrap.setPipeline(ClientContext.handshakePipeline);

		bootstrap.setOption("keepAlive", true);
		bootstrap.setOption("tcpNoDelay", true);

		ChannelFuture connectionFeedback = bootstrap
				.connect(new InetSocketAddress(host, port));
		
		/* Lets wait for the FutureChannel to receive an answer, 
		 * if no answer was given in 5 seconds it returns a ConnectTimeoutException */
		if(!connectionFeedback.awaitUninterruptibly(timeout)) {
			throw new ConnectTimeoutException("Connection Timed Out.\n" +
					  "Timeout Limit: " + timeout + "ms.");
		} else if (!connectionFeedback.isSuccess()) {
			throw new ServerOfflineException();
		}
		if(connectionFeedback.isSuccess()) {
			isConnected = true;
		}
	}
	
	/** Returns true if the client is successfully connected to the server. */
	public boolean isConnected() {
		return isConnected;
	}
	
	/** Frees all resources used by the bootstrap. */
	public void shutdown() {
		bootstrap.releaseExternalResources();
	}
	
	/**
	 * This function will connect to the server,<br/>
	 * <b>Note:</b> It will not return before the connection was completed,
	 * if the connection fails, two exceptions will be thrown:
	 * <ul>
	 * <li>ConnectionTimeoutException</li>
	 * <li>ServerOfflineException.</li>
	 * </ul>
	 * @param host: The server's IP.
	 * @param port: The port that the server is listening to.
	 */
	public void connect(String host, int port)
			throws ConnectTimeoutException, ServerOfflineException {
		connect(host, port, ClientContext.DEFAULT_CONNECTION_TIMEOUT_LIMIT);
	}
	
}

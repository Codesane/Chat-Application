package gui.client.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import network.client.app.HostConnectionCallback;
import network.client.handlers.ClientHandshakeHandler.HandshakeResponseListener;
import network.client.util.ClientContext;
import network.client.util.ClientContext.LoginStatus;
import network.client.util.ClientContext.ServerStatus;
import network.client.util.RecentConnections;
import network.global.handshake.HandshakeResponseEvent;
import network.global.handshake.HandshakeResponseEvent.HandshakeStatus;

public class LoginWindow extends JFrame implements ActionListener, HandshakeResponseListener {
	private static final long serialVersionUID = 1569577963433672973L;
	
	private JTextField username = new JTextField(15);
	private JPasswordField password = new JPasswordField(15);
	private JButton loginButton = new JButton("Login");
	
	private JPanel bottomInfoBar = new JPanel(new BorderLayout());
	
	private JLabel serverStatusLabel = new JLabel("<html>    Server Status: <a font color=\"#e1d519\">Connecting...</a></html>");
	
	private JPanel loginInputHolder = new JPanel(new GridBagLayout());
	
	private JLabel displayActionMessage = new JLabel("");
	
	private JMenuBar menuBar = new JMenuBar();
	private JMenu connect = new JMenu("Connect");
	private JMenuItem connectNew = new JMenuItem("New...");
	private JMenu connectRecent = new JMenu("Recent");
	
	/** Called when the login returns a successful value. */
	private LoginSuccessfulCallback loginSuccessfulCallback;
	private HostConnectionCallback hostConnectionCallback;
	
	private boolean canTryLogin = false;
	
	public LoginWindow(LoginSuccessfulCallback loginListener, HostConnectionCallback hostcallback) {
		super("Chat Client 1.0.0");
		
		this.loginSuccessfulCallback = loginListener;
		this.hostConnectionCallback = hostcallback;
		
		setSize(380, 270);
		RecentConnections.init();
		setupMenu();
		
		/* This will be listening for handshake responses and display information to the user.. */
		ClientContext.getHandshakeHandler().setHandshakeResponseListener(this);
		
		setMinimumSize(new Dimension(320, 270));
		
		/* There is a shutdwown hook in the client launcher that
		 * will disconnect the client. */
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		/* http://bugs.sun.com/view_bug.do?bug_id=6464548 
		 * Can be fixed manually with a WindowAdapter/Listener.*/
		setMaximumSize(new Dimension(420, 360));
		////////////////////////////////////////////////////
		setResizable(false);
		
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		populateInfoBar();
		add(bottomInfoBar, BorderLayout.SOUTH);
	}
	
	/** Sets up a JMenuBar with items and listeners. */
	private void setupMenu() {
		setJMenuBar(menuBar);
		menuBar.add(connect);
		connect.add(connectNew);
		connectNew.setToolTipText("Create a new connection.");
		connectNew.addActionListener(this);
		connect.add(connectRecent);
		if(RecentConnections.hasRecentConnections()) {
			ArrayList<InetSocketAddress> recentConnections = RecentConnections.getClientRecent();
			for(InetSocketAddress isa : recentConnections) {
				expandRecentConnectionsMenu(isa.getHostName() + ":" + isa.getPort());
			}
		} else {
			connectRecent.setEnabled(false);
			connectRecent.setToolTipText("You do not have any recent connections.");
		}
	}
	private void expandRecentConnectionsMenu(String newRecentConnection) {
		connectRecent.setEnabled(true);
		final JMenu recentConMenu = new JMenu(newRecentConnection);
		
		final JMenuItem connectToRecent = new JMenuItem("Connect");
		final JMenuItem removeRecent = new JMenuItem("Remove");
		recentConMenu.add(connectToRecent);
		recentConMenu.add(removeRecent);
		
		connectToRecent.setActionCommand(newRecentConnection);
		removeRecent.setActionCommand(newRecentConnection);
		
		connectToRecent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				connectToUnparsedAddress(e.getActionCommand());
			}
		});
		
		removeRecent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] hostNameIp = e.getActionCommand().split(":");
				if(hostNameIp.length == 2) { // For security precautions.
					InetSocketAddress remAddr = new InetSocketAddress(hostNameIp[0], Integer.parseInt(hostNameIp[1]));
					connectRecent.remove(recentConMenu);
					RecentConnections.removeConnection(remAddr);
					if(!RecentConnections.hasRecentConnections()) {
						connectRecent.setEnabled(false);
					}
				}
			}
		});
		
		connectRecent.add(recentConMenu);
	}
	
	/** Sets the bottom text of the  */
	public synchronized void setServerStatusInfo(final ServerStatus status) {
		if(status == ServerStatus.ONLINE) {
			/* The user will only be allowed to try to login if there is a connection with the server. */
			canTryLogin = true;
		} else {
			canTryLogin = false;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				serverStatusLabel.setText("<html>&nbsp;&nbsp;&nbsp;&nbsp;Server Status: " +
						"<a font color='" + status.getColor() + "'>" + status + "</a></html>");
			}
		});
	}
	
	/** Sets the actions text (the label located just below the login button) */
	public void setLoginActionInfoText(final LoginStatus status) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				displayActionMessage.setText(status.getDisplayText());
			}
		});
	}
	
	
	private void populateInfoBar() {
		bottomInfoBar.add(serverStatusLabel, BorderLayout.WEST);
		bottomInfoBar.setPreferredSize(new Dimension(getSize().width, 40));
		bottomInfoBar.setBackground(new Color(0xffffff));
		
		username.addActionListener(this);
		password.addActionListener(this);
		loginButton.addActionListener(this);
		
		GridBagConstraints c = new GridBagConstraints();
		loginInputHolder.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 25));
		
		add(loginInputHolder, BorderLayout.CENTER);
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		c.gridwidth = 2;
		
		c.anchor = GridBagConstraints.PAGE_START;
		
		JLabel loginLabel = new JLabel("Login");
		loginLabel.setFont(new Font("Arial", Font.BOLD, 32));
		
		c.insets = new Insets(0, 0, 15, 0);		
		loginInputHolder.add(loginLabel, c);
		
		
		/* Starting the Labels and input fields. 
		 * Username label. */
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.LINE_START;
		c.gridy = 1;
		loginInputHolder.add(new JLabel("Username: "), c);
		//
		
		/* Username Input Field */
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		loginInputHolder.add(username, c);
		//
		
		/* Password Label */
		c.gridy = 2;
		c.gridx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		loginInputHolder.add(new JLabel("Password: "), c);
		//
		
		/* Password Input Field */
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		loginInputHolder.add(password, c);
		//
		
		/* Login Button */
		c.gridy = 3;
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		loginInputHolder.add(loginButton, c);
		//
		
		c.gridy = 4;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.PAGE_END;
		displayActionMessage.setForeground(new Color(0xAD0009));
		c.insets = new Insets(10, 0, 0, 0);
		loginInputHolder.add(displayActionMessage, c);
		
	}
	
	/** Displays the window. */
	public void displayWindow() {
		setVisible(true);
	}
	
	/** Destroys the window, will make it unusable again. */
	public void destroy() {
		dispose();
	}
	
	/** Enables/disables the username and password field depending on the boolean,
	 *  @param enabled If set to true, the user will be able to query the server again,
	 *  will also select the password fields content and request focus. */
	private void enableUserInput(boolean enabled) {
		username.setEnabled(enabled);
		password.setEnabled(enabled);
		loginButton.setEnabled(enabled);
		if(enabled) {
			password.selectAll();
			password.requestFocus();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == loginButton || e.getSource() == username || e.getSource() == password) {
			if(!canTryLogin) {
				JOptionPane.showMessageDialog(null, "Unresolved Hostname!\n" +
						"Please supply a Server Hostname and Port before " +
						"logging in.", "Error: Unresolved Server.", JOptionPane.ERROR_MESSAGE);
				return;
			}
			enableUserInput(false);
			ClientContext.getHandshakeHandler().sendHandshakeRequest(username.getText(),
														  new String(password.getPassword()));
			setLoginActionInfoText(LoginStatus.HANDSHAKING);
		} else if(e.getSource() == connectNew) {
			InetSocketAddress mostRecentConn = RecentConnections.getMostRecent();
			String defaultConnectionString = "aegidius.se:13337";
			if(mostRecentConn != null) {
				defaultConnectionString = mostRecentConn.getHostName() + ":" + mostRecentConn.getPort();
			}
			String hostInput = JOptionPane.showInputDialog("Please enter the new Hostname and Port " +
					"separated by colon.\nExample: hostname:port", defaultConnectionString);
			connectToUnparsedAddress(hostInput);
		}
	}
	
	/**
	 * Will ask to connect to the address given as a string.
	 * The expected format is: <b>host:port</b> */
	private void connectToUnparsedAddress(String unparsed) {
		
		if(unparsed == null) return;
		String[] parsedHost = unparsed.split(":");
		if(parsedHost.length == 2) {
			try {
				if(RecentConnections.addNewConnection(new InetSocketAddress(parsedHost[0], Integer.parseInt(parsedHost[1])))) {
					expandRecentConnectionsMenu(parsedHost[0] + ":" + Integer.parseInt(parsedHost[1]));
				}
				hostConnectionCallback.connectToHost(parsedHost[0], Integer.parseInt(parsedHost[1]));
			} catch(Exception numExc) {
				JOptionPane.showMessageDialog(null, "Invalid Format: " + parsedHost[1] + " is not a valid port number.",
						"Not a Valid Port Number", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	@Override
	public void handshakeResponseReceived(HandshakeResponseEvent e) {
		if(e.getStatus() == HandshakeStatus.FAILED) {
			enableUserInput(true);
			switch(e.getCause()) {
				case HandshakeResponseEvent.CAUSE_USERNAME_PASSWORD_NOMATCH:
					setLoginActionInfoText(LoginStatus.ERROR_USERNAME_PASSWORD);
				break;
				case HandshakeResponseEvent.CAUSE_INCORRECT_VERSION:
					setLoginActionInfoText(LoginStatus.ERROR_VERSION);
				break;
				case HandshakeResponseEvent.CAUSE_INCORRECT_KEY:
					setLoginActionInfoText(LoginStatus.ERROR_KEY);
				break;
				case HandshakeResponseEvent.CAUSE_USER_BANNED:
					setLoginActionInfoText(LoginStatus.BANNED);
				break;
			}
		} else if(e.getStatus() == HandshakeStatus.SUCCESSFUL) {
			// Be happy.
			setLoginActionInfoText(LoginStatus.SUCCESSFUL);
			loginSuccessfulCallback.loginSuccessful(e);
		}
	}
	public interface LoginSuccessfulCallback {
		public void loginSuccessful(HandshakeResponseEvent e);
	}
}

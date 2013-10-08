package gui.client.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import network.client.util.ClientContext;
import network.client.util.Users;
import network.global.events.Events;
import network.global.events.NetworkEvent;
import network.global.messages.BroadcastMessage;
import network.global.messages.PrivateMessage;
import network.global.objects.NetworkSharedUser;
import network.global.update.UpdateHeaderEvent;
import network.global.update.WelcomeMessage;

@SuppressWarnings("serial")
public class ClientWindow extends JFrame implements ActionListener {
	
	private JList<String> usersOnlineList = new JList<String>();
	private DefaultListModel<String> model = new DefaultListModel<String>();
	
	private JTabbedPane openChatsPane = new JTabbedPane();
	private Hashtable<Integer, ChatWindow> privateChats = new Hashtable<Integer, ChatWindow>();
	
	private ChatWindow globalChat = new ChatWindow(-1);
	
	
	private JPopupMenu userSelectMenu = new JPopupMenu();
	private JMenuItem userPrivateMessageItem = new JMenuItem("Private Message");
	
	private NetworkSharedUser thisUser;
	
	public ClientWindow() {
		super("Simple Chat Application v1.0.0");
		setSize(640, 480);
		setMinimumSize(new Dimension(420, 340));
		
		/** Caught by the shutdown hook lateron. */
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		usersOnlineList.setModel(model);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		
		userSelectMenu.add(userPrivateMessageItem);
		userPrivateMessageItem.addActionListener(this);
		usersOnlineList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		add(openChatsPane, BorderLayout.CENTER);
		add(usersOnlineList, BorderLayout.EAST);
		
		openChatsPane.addTab("Broadcast", globalChat);
		
		
		openChatsPane.setBorder(BorderFactory.createTitledBorder("Chats: "));
		
		usersOnlineList.setBorder(BorderFactory.createTitledBorder("Online: "));
		usersOnlineList.setPreferredSize(new Dimension(200, getSize().height));
		usersOnlineList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				/* Will show a popup for the user. */
				if(SwingUtilities.isRightMouseButton(e)) {
					usersOnlineList.setSelectedIndex(usersOnlineList.locationToIndex(e.getPoint()));
					userSelectMenu.show(usersOnlineList, e.getPoint().x, e.getPoint().y);
				}
			}	
		});
	}
	
	/** Triggered when a user has logged out.
	 *  Will return if the thisUser variable is null
	 *  because we're waiting for the sync and there's no need to do anything then. */
	public void onUserLogout(NetworkSharedUser user) {
		if(thisUser == null) return;
		if(privateChats.get(user.getId()) != null) {
			privateChats.get(user.getId()).userDisconnected(user.getName());
		}
		globalChat.addText(user.getName() + " has logged out.");
		model.removeElement(user.getName());
	}
	
	/** Triggered when a new user has logged in. */
	public void onUserLogin(NetworkSharedUser user) {
		if(thisUser == null) return;
		
		globalChat.addText(user.getName() + " has logged in.");
		model.addElement(user.getName());
	}
	
	/** Shows the welcome message in the Broadcast window and sets the thisUser variable to the
	 *  user data received by the server. */
	public void onWelcomeMessageReceived(WelcomeMessage welcomeMessage) {
		globalChat.addText(welcomeMessage.getWelcomeMessage() + "\n\n");
		thisUser = welcomeMessage.getUser();
		super.setTitle("Chat - " + thisUser.getName());
		NetworkEvent syncReadyEvent = Events.createNewEvent(ClientContext.clientChannel);
		syncReadyEvent.setMessage(new UpdateHeaderEvent(UpdateHeaderEvent.USER_SYNC_READY));
		syncReadyEvent.send();
	}
	
	/** Returns the NetworkSharedUser class with
	 *  data concerning THIS user. */
	public NetworkSharedUser getUser() {
		return thisUser;
	}
	
	/** Called by the ClientGuiManager class, appends a broadcast message
	 *  to the Broadcast ChatWindow. */
	public void onBroadcastMessageReceived(BroadcastMessage msg) {
		globalChat.addText("[" + msg.getSender() + "]: " + " " + msg.getMessage() + 
						  (msg.getMessage().endsWith("\n") ? "" : "\n"));
	}
	
	/** Opens a new chat window and sets the tab name to the user parameter
	 *  the id supplied will be the ID the client attempts to send the message to.
	 *  @param isUserAction If true, this means that the user explicitly tried to open the window,
	 *  this will have the window focused when triggered. */
	private void openChatWindow(String user, int userId, boolean isUserAction) {
		if(Users.getUsersOnline() != null) {
			for(NetworkSharedUser n : Users.getUsersOnline()) {
				if(n.getName().equalsIgnoreCase(user)) {
					if(privateChats.get(userId) == null) {
						ChatWindow newChat = new ChatWindow(userId);
						privateChats.put(userId, newChat);
						openChatsPane.addTab(null, newChat);
						
						int tabIndex = openChatsPane.getTabCount() - 1;
						
						ChatsPaneTab chatTab = new ChatsPaneTab(user, tabIndex);
						chatTab.setMaximumSize(new Dimension(350, 20));
						openChatsPane.setTabComponentAt(tabIndex, chatTab);
						chatTab.setActionListener(this);
						if(isUserAction) {
							openChatsPane.setSelectedIndex(tabIndex);
						}
					} else {
						if(isUserAction) {
							for(int i = 0; i < openChatsPane.getTabCount(); i++) {
								if(openChatsPane.getTitleAt(i).equalsIgnoreCase(user)) {
									openChatsPane.setSelectedIndex(i);
								}
							}
						}
					}
					break;
				}
			}
		}
	}
	
	/** Called by the ClientGuiManager class when a private message was received. */
	public void onPrivateMessageReceived(PrivateMessage pm) {
		openChatWindow(pm.getSender(), pm.getFromId(), false);
		privateChats.get(pm.getFromId()).onPrivateMessageReceived(pm);
	}
	
	/** Returns the userID given the name of the user,
	 *  queries the Users class for the id.
	 *  @deprecated I will move this function to the Users class in the future */
	public int getUserIdByName(String name) {
		if(Users.getUsersOnline() == null) return -1;
		for(NetworkSharedUser nsu : Users.getUsersOnline()) {
			if(nsu.getName().equalsIgnoreCase(name)) {
				return nsu.getId();
			}
		}
		return -1;
	}
	
	/** Syncing the user online list, removing everything in the list and replaces it
	 *  with the array from the server. */
	public void syncUsers(ArrayList<NetworkSharedUser> users) {
		model.removeAllElements();
		for(NetworkSharedUser u : Users.getUsersOnline()) {
			
			// We don't want to add ourself to the online list.
			if(u.getId() == thisUser.getId()) continue;
			model.addElement(u.getName());
		}
	}
	
	public void displayWindow() {
		setVisible(true);
	}
	
	private void closeTab(String actionCommand) {
		String[] parseInputAC = actionCommand.split(":");
		if(parseInputAC == null || parseInputAC.length != 2) {
			System.err.println("Error closing tab");
			return;
		}
		NetworkSharedUser nsu = Users.getUserByName(parseInputAC[0]);
		// Nsu will be null if the user disconnected.
		if(nsu != null) {
			if(JOptionPane.showConfirmDialog(null, "Are you sure you want to:\nClose private chat with "
					+ nsu.getName() + "?", "Close Chat?", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
				return;
			}
			privateChats.remove(nsu.getId());
		}
		
		openChatsPane.remove(Integer.parseInt(parseInputAC[1]));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == userPrivateMessageItem) {
			String toUser = usersOnlineList.getSelectedValue();
			openChatWindow(toUser, getUserIdByName(toUser), true);
		} else if(e.getSource() instanceof JButton) {
			closeTab(((JButton) e.getSource()).getActionCommand());
		}
	}
	private class ChatsPaneTab extends JPanel {
		
		private JLabel username;
		
		private JButton closeButton = new JButton("x");
		
		public ChatsPaneTab(String name, int tabIndex) {
			super(new BorderLayout());
			username = new JLabel(name);
			setOpaque(false);
			closeButton.setBackground(new Color(0xcccccc));
			
			closeButton.setPreferredSize(new Dimension(30, getPreferredSize().height - 10));
			setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 2));
			closeButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
			closeButton.setOpaque(false);
			closeButton.setForeground(new Color(0xff0000));
			
			add(username, BorderLayout.CENTER);
			closeButton.setActionCommand(username.getText() + ":" + tabIndex);
			add(closeButton, BorderLayout.EAST);
		}
		
		public void setActionListener(ActionListener al) {
			closeButton.addActionListener(al);
		}
	}
}

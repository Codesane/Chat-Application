package gui.client.app;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

import network.client.util.ClientContext;
import network.client.util.Users;
import network.global.events.Events;
import network.global.events.NetworkEvent;
import network.global.messages.BroadcastMessage;
import network.global.messages.PrivateMessage;
import network.global.objects.NetworkSharedUser;

@SuppressWarnings("serial")
public class ChatWindow extends JPanel implements ActionListener {
	
	private JTextArea displayTextArea = new JTextArea();
	private JScrollPane scrollPane = new JScrollPane(displayTextArea);
	/** Used to get the length of the document so that we can select the document and drag
	 *  the scrollbar to the end. */
	private Document displayTextAreaDoc = displayTextArea.getDocument();
	
	private JTextField inputText = new JTextField();
	
	/** The user-id that the message will be sent to. */
	private int targetUserId = 0;
	
	/** A chat instance to be added to the TabbedPane,
	 *  contains a window and input handler, manages the 
	 *  network events on its own so there's no need to attach any handlers
	 *  or callbacks to this class. */
	public ChatWindow(int targetUserId) {
		super(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
		add(inputText, BorderLayout.SOUTH);
		inputText.addActionListener(this);
		displayTextArea.setEditable(false);
		displayTextArea.setLineWrap(true);
		displayTextArea.setWrapStyleWord(true);
		this.targetUserId = targetUserId;
		
	}
	
	/** Gui-Thread-safe adding of text to the textarea. */
	public void addText(final String[] text) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				synchronized(displayTextArea) {
					for(String t : text) {
						displayTextArea.append(t + (t.endsWith("\n") ? "" : "\n"));
						setScrollbarAtBottom();
					}
				}
			}
		});
	}
	
	/** Sets the vertical (and only) scrollbar at the bottom
	 *  of the document. */
	public void setScrollbarAtBottom() {
		displayTextArea.select(displayTextAreaDoc.getLength(),
	   			   			   displayTextAreaDoc.getLength());
	}
	
	/** Triggered by ClientGuiManager. */
	public void userDisconnected(String userName) {
		addText(userName + " has Disconnected.");
		inputText.setEnabled(false);
		
	}
	
	/** Adds a single line of text to the window, calls the
	 *  thread safe function. */
	public void addText(String text) {
		addText(new String[]{text});
	}
	
	/** Triggered by the ClientGuiManager */
	public void onPrivateMessageReceived(PrivateMessage pm) {
		if(pm.getMessage() != null && pm.getSender() != null) {
			addText("[" + pm.getSender() + "]: " + pm.getMessage() + 
					(pm.getMessage().endsWith("\n") ? "" : "\n"));
		}
	}
	
	/** Triggered by the ClientGuiManager */
	public void onBroadcastMessageReceived(BroadcastMessage bm) {
		if(bm.getMessage() != null) {
			addText("[" + bm.getSender() + "]: " + bm.getMessage() +
					(bm.getMessage().endsWith("\n") ? "" : "\n"));
		}
	}
	
	
	/** Resets the input field and requests thefield focus. */
	private void resetInputField() {
		inputText.setText("");
		inputText.requestFocus();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == inputText) {
			if(inputText.getText().trim().equals("")) { resetInputField(); return;}
			/** Look for it every time, if it's not there he's probably offline. */
			NetworkSharedUser sendTo = Users.getUserById(targetUserId);
			if(sendTo == null) {
				if(targetUserId == -1) {
					NetworkEvent messageEvent = Events.createNewEvent(ClientContext.clientChannel);
					BroadcastMessage broadCast = new BroadcastMessage(inputText.getText());
					messageEvent.setMessage(broadCast);
					messageEvent.send();
					resetInputField();
				}
			} else {
				// If the target user id is -1, it's the global channel.
				NetworkEvent messageEvent = Events.createNewEvent(ClientContext.clientChannel);
				PrivateMessage pm = new PrivateMessage(targetUserId, inputText.getText());
				messageEvent.setMessage(pm);
				messageEvent.send();
				addText("[You]: " + inputText.getText());
				resetInputField();
			}
		}
	}
}
	
	

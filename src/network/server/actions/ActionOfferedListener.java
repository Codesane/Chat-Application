package network.server.actions;

public interface ActionOfferedListener {
	/** A listener for the GUI class that actions has been added and that it's now possible
	 *  to empty the buffer when the GUI thread finishes and render them the next time the
	 *  GUI thread is run. */
	public void actionOffered();
}

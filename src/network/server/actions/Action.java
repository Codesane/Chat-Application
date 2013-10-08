package network.server.actions;

import network.server.actions.Actions.ActionType;

public class Action {
	
	private int actionId = -1;
	
	private ActionType type;
	
	private String message;
	
	private long logTime;
	
	/** @param type: The kind of message that will be displayed in the output window of the server.
	 *  @param message: The message to be shown (Information about what the action was).
	 *  @param time: The time that this action was performed. */
	public Action(ActionType type, String message, long time) {
		this.type = type;
		this.message = message;
		this.logTime = time;
	}
	
	/** Unique id for this action. */
	protected void setId(int id) {
		actionId = id;
	}
	
	/**  */
	public int getId() {
		return actionId;
	}
	
	/** @param type: The kind of message that will be displayed in the output window of the server.
	 *  @param message: The message to be shown (Information about what the action was). */
	public Action(ActionType type, String message) {
		this(type, message, System.currentTimeMillis());
	}
	
	/** Returns the time the Action took place. */
	public long getTime() {
		return this.logTime;
	}
	
	/** Returns an enum containing the color that the message should be displayed with. */
	public ActionType getType() {
		return this.type;
	}
	
	/** Returns the message string to be displayed in the console. */
	public String getMessage() {
		return this.message;
	}
	
}

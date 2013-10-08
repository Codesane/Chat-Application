package network.server.actions;

import java.awt.Color;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Actions {
	
	/** The time in milliseconds that the server was started. */
	private static long startTime;
	
	/** Time in milliseconds that the init process ended. */
	private static long initDoneTime;
	
	/** Enum representing the different logs. */
	public static enum ActionType {
		INIT(new Color(0xffffff)), LOG(new Color(0x000000)), INFO(new Color(0x000000)), WARNING(new Color(0x000000)),
		ERROR(new Color(0x000000)), SEVERE(new Color(0x000000));
		
		final Color color;
		
		ActionType(Color c) {
			this.color = c;
		}
	}
	
	/**  */
	private static volatile int curActionId = 0;
	
	private static ActionOfferedListener offeredListener;
	
	private static Queue<Action> actionsBuffer = new ConcurrentLinkedQueue<Action>();
	
	public static void addAction(Action newAction) {
		newAction.setId(curActionId++);
		boolean offerSuccessful = actionsBuffer.offer(newAction);
		if(offerSuccessful && offeredListener != null) {
			offeredListener.actionOffered();
		}
	}
	
	/** Adds a listener when an action has been added,
	 *  this is for the GUI thread, instead of having a single listener
	 *  supplying the GUI thread with many actions and thus invoking many threads
	 *  we invoke only one thread with the purpose of clearing the buffer and displaying
	 *  it to the Server console. */
	public static void setListener(ActionOfferedListener offerListener) {
		offeredListener = offerListener;
	}
	
	/** Removes the head of the LinkedQueue */
	public static Action take() {
		Action action = actionsBuffer.poll();
		ActionLogger.log(action.getMessage());
		return action;
	}
	
	/** Returns true if the LinkedQueue isn't empty */
	public static boolean hasActions() {
		return !actionsBuffer.isEmpty();
	}
	
	/** Sets the initialization timestamp(Doesn't do anything but set a timestamp) */
	public static void initStart() {
		ActionLogger.initLogger(true, 5);
		startTime = System.currentTimeMillis();
	}
	
	/** Called when the initialization is done. */
	public static void initDone() {
		initDoneTime = System.currentTimeMillis();
		Actions.addAction(new Action(ActionType.INFO, "Init Time: " + (initDoneTime - startTime) + "ms."));
	}
	
}

package network.server.app;

import network.server.actions.Action;
import network.server.actions.ActionOfferedListener;
import network.server.actions.Actions;
import network.server.actions.Actions.ActionType;

/** While no gui is available. */
public class ServerConsoleOutputter implements ActionOfferedListener{
	
	/** Just having the LOG as default value for this one. */
	private ActionType prevActionType = ActionType.LOG;
	
	@Override
	public void actionOffered() {
		while(Actions.hasActions()) {
			Action a = Actions.take();
			String aMsg = "[" + a.getType().toString() + " => t :: 0x" + Long.toHexString(a.getTime()) + " => aId :: 0x" + Integer.toHexString(a.getId()) + "]" +
					" " + a.getMessage();
			if(a.getType() == Actions.ActionType.SEVERE) {
				if(prevActionType == ActionType.INIT) System.err.println("[FAILED]");
				System.err.println(aMsg);
				System.exit(-1);
			} else {
				if(prevActionType == ActionType.INIT) System.out.println("[OK]");
				if(a.getType() == ActionType.INIT) System.out.print(aMsg);
				else System.out.println(aMsg);
			}
			prevActionType = a.getType();
		}
	}
}

package network.server.handshakeutil;

import network.global.handshake.HandshakeRequestEvent;
import network.global.handshake.HandshakeResponseEvent.HandshakeStatus;
import network.global.handshake.ValidatedHandshakeResponse;
import network.server.database.DBQueryUserResult;
import network.server.database.DBConstants;
import network.server.database.Database;
import network.server.sessions.Session;
import network.server.sessions.Sessions;

import org.jboss.netty.channel.Channel;

public final class HandshakeValidator {
	
	/** Returns a response that can be sent to the User. */   
	public static synchronized ValidatedHandshakeResponse validate(HandshakeRequestEvent handshakeRequestEvent, Channel c) {
		ValidatedHandshakeResponse vhr = null;
		
		short errCode = matchVersionAndKey(handshakeRequestEvent.getVersion(),
										   handshakeRequestEvent.getAccessKey());
		
		if(errCode != ValidatedHandshakeResponse.NO_ERROR) {
			return handshakeFailedMessage(errCode);
		}
		
		DBQueryUserResult dbQuery = 
				Database.getUser(handshakeRequestEvent.getUsername(), 
								 handshakeRequestEvent.getPassword());
		
		if(dbQuery == null) {
			return handshakeFailedMessage(ValidatedHandshakeResponse.CAUSE_USERNAME_PASSWORD_NOMATCH);
		} else if(dbQuery.getIsBanned()) {
			return handshakeFailedMessage(ValidatedHandshakeResponse.CAUSE_USER_BANNED);
		}
		
		// TODO: Add the user to the Sessions.
		Sessions.addSession(new Session(dbQuery.getAsUser(), c));
		
		vhr = new ValidatedHandshakeResponse(dbQuery.getId());
		
		return vhr;
	}
	
	/** Will return a the cause if the version or key is incorrect,
	 *  returns 0x00 (NO_ERROR) otherwise. */
	private static short matchVersionAndKey(String version, String key) {
		
		/* If the version of the client sent with the handshake is incorrect this statement will trigger. */
		if(!Database.getSettingsOption(DBConstants.SETTINGS_SERVER_VERSION).equals(version)) {
			return ValidatedHandshakeResponse.CAUSE_INCORRECT_VERSION;
		}
		/* If the key sent with the handshake is incorrect, this statement will trigger. */
		if(!Database.getSettingsOption(DBConstants.SETTINGS_SERVER_AUTH_KEY).equals(key)) {
			return ValidatedHandshakeResponse.CAUSE_INCORRECT_KEY;	
		}
		return ValidatedHandshakeResponse.NO_ERROR;
	}
	
	/** Will be called if the handshake fails, generates the response with
	 *  the cause. */
	private static ValidatedHandshakeResponse handshakeFailedMessage(short cause) {
		return new ValidatedHandshakeResponse(HandshakeStatus.FAILED, -1, cause);
	}
	
}

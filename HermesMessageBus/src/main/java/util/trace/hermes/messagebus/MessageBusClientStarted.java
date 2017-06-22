package util.trace.hermes.messagebus;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.json.JSONObject;

import hermes.ClientThread;
import util.trace.TraceableInfo;

public class MessageBusClientStarted extends TraceableInfo {
	public MessageBusClientStarted(String aMessage, Object aFinder,
			ClientThread aClient ) {

		super(aMessage, aFinder);
	}
	public static MessageBusClientStarted newCase(			
			Object aFinder,
			ClientThread aClient
			) { 
		
		String aMessage = "Client:" + 
		aClient;
		
		MessageBusClientStarted retVal = new MessageBusClientStarted(
				aMessage, aFinder, aClient);				
    	retVal.announce();
    	return retVal;
	}
}

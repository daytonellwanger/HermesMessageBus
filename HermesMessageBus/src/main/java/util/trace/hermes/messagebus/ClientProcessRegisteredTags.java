package util.trace.hermes.messagebus;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.json.JSONObject;

import hermes.ClientThread;
import util.trace.TraceableInfo;

public class ClientProcessRegisteredTags extends TraceableInfo {
	public ClientProcessRegisteredTags(String aMessage, Object aFinder,
			ClientThread aClient, String aTags ) {

		super(aMessage, aFinder);
	}
	public static ClientProcessRegisteredTags newCase(			
			Object aFinder,
			ClientThread aClient, String aTag
			) { 
		
		String aMessage = "Client:" + 
		aClient + " tags regex: " + aTag;
		
		ClientProcessRegisteredTags retVal = new ClientProcessRegisteredTags(
				aMessage, aFinder, aClient, aTag);				
    	retVal.announce();
    	return retVal;
	}
}

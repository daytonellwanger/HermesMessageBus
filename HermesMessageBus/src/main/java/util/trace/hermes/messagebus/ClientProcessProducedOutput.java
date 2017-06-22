package util.trace.hermes.messagebus;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.json.JSONObject;

import hermes.ClientThread;
import util.trace.TraceableInfo;

public class ClientProcessProducedOutput extends TraceableInfo {
	public ClientProcessProducedOutput(String aMessage, Object aFinder,
			String aClient, String anOutput ) {

		super(aMessage, aFinder);
	}
	public static ClientProcessProducedOutput newCase(			
			Object aFinder,
			String aClient, String anOutput
			) { 
		
		String aMessage = 
		aClient + ":" + anOutput;
		
		ClientProcessProducedOutput retVal = new ClientProcessProducedOutput(
				aMessage, aFinder, aClient, anOutput);				
    	retVal.announce();
    	return retVal;
	}
}

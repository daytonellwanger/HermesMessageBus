package hermes.remote;

import java.rmi.Remote;

public interface RemoteCentralRegistry extends Remote{
	static int MESSAGE_BUS_PORT = 4999;
	static String MESASAGE_BUS_NAME = "message bus";

}

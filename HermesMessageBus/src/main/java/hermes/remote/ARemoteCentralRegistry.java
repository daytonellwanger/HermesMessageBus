package hermes.remote;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import hermes.CentralFragmentedXMPP;
//import inputport.rpc.GIPCLocateRegistry;
//import inputport.rpc.GIPCRegistry;

public class ARemoteCentralRegistry extends CentralFragmentedXMPP implements RemoteCentralRegistry {
	public static void main(String[] args) {
		new ARemoteCentralRegistry().init();
	}
	@Override
	protected void init() {

		super.init();
		try {
			ServerSocket aServerSocket = new ServerSocket(MESSAGE_BUS_PORT);
//			aServerSocket.
//			GIPCRegistry gipcRegistry = GIPCLocateRegistry.createRegistry(MESSAGE_BUS_PORT);
//			RemoteCentralRegistry aMessageBus = new ARemoteCentralRegistry();
//			gipcRegistry.rebind(MESASAGE_BUS_NAME, aMessageBus);
			
		} catch (Exception e) {
			e.printStackTrace();
		}		

	}
	class ASocketAcceptingRunnable implements Runnable {
		ServerSocket serverSocket;
		ARemoteCentralRegistry central;
		public  ASocketAcceptingRunnable(ARemoteCentralRegistry aCentral, ServerSocket aServerSocket) {
			serverSocket = aServerSocket;
			central = aCentral;
		}

		@Override
		public void run() {
			while (true) {
				try {
					Socket aSocket = serverSocket.accept();
					SocketOrIOClientManager aClient = new SocketOrIOClientManager(central, aSocket);
					clients.add(aClient);
					
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
}
}

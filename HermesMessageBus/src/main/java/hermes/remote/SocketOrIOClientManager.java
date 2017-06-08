package hermes.remote;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import hermes.Central;
import hermes.ClientThread;

public class SocketOrIOClientManager extends ClientThread {

	public SocketOrIOClientManager(String name, Process process, Central central) {
		super(name, process, central);
	}
	public SocketOrIOClientManager(Central central, Socket aSocket) {
		super(central);

	}
	protected void init(Socket aSocket) {
		try {
			inputStream = new StreamThread(aSocket.getInputStream());
		
//		errorStream = new StreamThread(process.getErrorStream());
//		errorStream = new ErrorStreamThread(process.getErrorStream());

		output = new PrintWriter(new BufferedOutputStream(aSocket.getOutputStream()));
		(new Thread(inputStream)).start();
//		(new Thread(errorStream)).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


}

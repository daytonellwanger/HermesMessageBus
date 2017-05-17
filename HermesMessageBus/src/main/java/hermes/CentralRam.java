package hermes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CentralRam extends Central implements StanzaListener, StanzaFilter {
//	
//	private static final String SERVER_PATHS_FILE = "servers.txt";
//	private static final String SERVER_FOLDER = "servers";
//	private static final String UNSPECIFIED_PROCESS_ID = "UNSPECIFIED";
//	private static final String PROCESS_ID_FIELD = "processId";
//	private static final String TAGS_FIELD = "tags";
//	private static final String NO_TAGS = "none";
//	
//	private List<String> serverPaths;
//	private List<ClientThread> servers;
	protected HashMap<Integer, List<String>> messageFragments;
//	
//	private ByteArrayOutputStream pausedOutput;
//	
//	private AbstractXMPPConnection connection;
//	

	public static void main(String[] args) {
		new CentralRam().init();
	}
	
	protected void init() {
//		serverPaths = new LinkedList<String>();
//		servers = new LinkedList<ClientThread>();
		messageFragments = new HashMap<Integer, List<String>>();
		super.init();
//		getServerPaths();
//		initServers();
//		initXMPP();
	}
//	
//	private void getServerPaths() {
//		serverPaths.addAll(getServerPathsFromFile());
//		serverPaths.addAll(getServerPathsFromFolder());
//	}
//	
//	private static List<String> getServerPathsFromFile() {
//		List<String> servers = new LinkedList<String>();
//		File pathsFile = new File(SERVER_PATHS_FILE);
//		try {
//			Scanner scanner = new Scanner(pathsFile);
//			while(scanner.hasNextLine()) {
//				servers.add(scanner.nextLine());
//			}
//			scanner.close();
//		} catch (FileNotFoundException e) {
//			System.out.println("Server paths file " + SERVER_PATHS_FILE + " not found.");
//			e.printStackTrace();
//		}
//		return servers;
//		
//	}
//	
//	private static List<String> getServerPathsFromFolder() {
//		List<String> servers = new LinkedList<String>();
//		File folder = new File(SERVER_FOLDER);
//		for(String file : folder.list()) {
//			String[] tokens = file.split("\\.");
//			if(tokens.length >= 2) {
//				if(tokens[tokens.length - 1].equalsIgnoreCase("jar")) {
//					servers.add(SERVER_FOLDER + "/" + file);
//				}
//			}
//		}
//		return servers;
//	}
//	
//	private void initServers() {
//		for(String jarPath : serverPaths) {
//			try {
//				initServer(jarPath);
//			} catch (Exception ex) {
//				System.out.println("Could not load " + jarPath);
//				ex.printStackTrace();
//			}
//		}
//	}
	
//	private void initServer(String exePath) throws Exception {
//		String fileExtension = exePath.substring(exePath.lastIndexOf(".") + 1);
//		ProcessBuilder builder;
//		if(fileExtension.equalsIgnoreCase("jar")) {
//			builder = new ProcessBuilder("java", "-jar", (new File(exePath)).getAbsolutePath());
//		} else if (fileExtension.equalsIgnoreCase("js")) {
//			builder = new ProcessBuilder("node", (new File(exePath)).getAbsolutePath());
//		} else {
//			return;
//		}
//		
//		File directory = (new File(exePath)).getParentFile();
//		if(directory != null) {
//			builder.directory(directory);
//		}
//		try {
//			Process serverProcess = builder.start();
//			ClientThread server = new ClientThread(exePath, serverProcess, this);
//			servers.add(server);
//		} catch (Exception ex) {
//			System.out.println("Failed to start server " + exePath);
//			ex.printStackTrace();
//		}
//	}
	
//	private void initXMPP() {
//		try {
//			Scanner scanner = new Scanner(System.in);
//			System.out.print("XMPP Username: ");
//			String[] usernameAndDomain = scanner.next().split("@");
//			String username = usernameAndDomain[0];
//			String domain = usernameAndDomain[1];
//			System.out.print("\nHost: ");
//			String host = scanner.next();
//			System.out.print("\nPassword: ");
//			String password = scanner.next();
//			System.out.println();
//			System.out.print("\nSecurity? ");
//			boolean security = scanner.next().toLowerCase().contains("y");
//			System.out.println();
//	
//			XMPPTCPConnectionConfiguration.Builder configBuilder = 
//					XMPPTCPConnectionConfiguration.builder();
//			configBuilder.setUsernameAndPassword(username, password);
//			configBuilder.setServiceName(domain);
//			configBuilder.setHost(host);
//			if(!security) {
//				configBuilder.setSecurityMode(SecurityMode.disabled);
//			}
//			
//			connection = new XMPPTCPConnection(configBuilder.build());
//			int loginAttempts = 0;
//			for(; loginAttempts < 10; loginAttempts++ ){
//				try {
//					connection.connect();
//					connection.login();
//					connection.addSyncStanzaListener(this, this);
//					System.out.println("XMPP Connected.");
//					break;
//				} catch (Exception ex) {
//					System.out.println("Failed to login. Remaining attempts: " + (9 - loginAttempts));
//					ex.printStackTrace();
//				}
//			}
//			if(loginAttempts == 10) {
//				scanner.close();
//				stopServers();
//				return;
//			}
//			
//			while(!scanner.next().equals("q"));
//			scanner.close();
//			stopServers();
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//	}
//	
//	private void stopServers() {
//		for(ClientThread server : servers) {
//			server.stop();
//		}
//	}
	
//	//to be called by client threads
//	public void receiveMessage(String message) {
//		JSONObject json;
//		try {
//			json = new JSONObject(message);
//			receiveMessage(json);
//		} catch (Exception ex) {}
//		
//	}
//	
//	public void sendMessage(String messageBody) {
//		JSONObject messageJSON = new JSONObject(messageBody);
//		String to = messageJSON.getString("to");
//		if(to == null) {
//			return;
//		}
//		if(connection.isConnected()) {
//			Message message = new Message(to);
//			message.setBody("0.true." + messageBody);
//			try { 
//				System.out.println("Sending message: " + message.toString());
//				connection.sendStanza(message);
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
//		}
//		
//	}
//	
//	private void receiveMessage(JSONObject message) {
//		System.out.println(message.toString());
//		if(!message.has(PROCESS_ID_FIELD)) {
//			message.put(PROCESS_ID_FIELD, UNSPECIFIED_PROCESS_ID);
//		}
//		forwardMessage(message);
//	}
//	
//	private void forwardMessage(JSONObject message) {
//		JSONArray tags;
//		if(!message.has(TAGS_FIELD)) {
//			tags = new JSONArray();
//			tags.put(NO_TAGS);
//		} else {
//			tags = (JSONArray) message.get(TAGS_FIELD);
//		}
//		boolean messageResponder = !message.has("CALLBACK_TAG");
//		for(ClientThread server : servers) {
//			for(int i = 0; i < tags.length(); i++) {
//				if(server.matchesTag(tags.getString(i))) {
//					server.receiveMessage(message.toString());
//					messageResponder = true;
//					break;
//				}
//			}
//		}
//		if(!messageResponder) {
//			sendNoResponse(message);
//		}
//	}
//	
//	private void sendNoResponse(JSONObject message) {
//		JSONArray tags = new JSONArray();
//		tags.put(message.getString("CALLBACK_TAG"));
//		message.put("RespondedTo", false);
//	}
//	
//	@Override
//	public boolean accept(Stanza stanza) {
//		return stanza instanceof Message;
//	}
//	
	protected void fullMessageReceived(String message) {
		try {
			JSONObject json = new JSONObject(message);
			receiveMessage(json);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void processPacket(Stanza stanza) throws NotConnectedException {
		Message message = (Message) stanza;
		try {
			String messageBody = message.getBody();
			int div1 = messageBody.indexOf(".", 0);
			int div2 = messageBody.indexOf(".", div1 + 1);
			int hash = Integer.parseInt(messageBody.substring(0, div1));
			boolean end = Boolean.parseBoolean(messageBody.substring(div1 + 1, div2));
			String prunedMessage = messageBody.substring(div2 + 1);
			List<String> mfrags = messageFragments.get(hash);
			
			if(end) {
				String fullMessage = "";
				if(mfrags != null) {
					for(String s : mfrags) {
						fullMessage += s;
					}
					messageFragments.remove(hash);
				}
				fullMessage += prunedMessage;
				fullMessageReceived(fullMessage);
			} else {
				if(mfrags == null) {
					mfrags = new LinkedList<String>();
					messageFragments.put(hash, mfrags);
				}
				mfrags.add(prunedMessage);
			}
		} catch (Exception ex) {
			System.out.println(message);
			System.out.println(message.getBody());
			ex.printStackTrace();
		}
	}
	
//	private void pauseOutput() {
//		pausedOutput = new ByteArrayOutputStream();
//		System.setOut(new PrintStream(pausedOutput));
//	}
//	
//	private void unpauseOutput() {
//		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
//		System.out.print(pausedOutput.toString());
//	}
//	
}

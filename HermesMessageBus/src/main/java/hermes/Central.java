package hermes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
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
import org.json.JSONObject;

public class Central implements StanzaListener, StanzaFilter {
	
	protected static final String CLIENT_PATHS_FILE = "clients.txt";
	protected static final String XMPP_CREDENTIALS_FILE = "xmppcredentials.txt";

	protected static final String CLIENT_FOLDER = "clients";
	protected static final String UNSPECIFIED_PROCESS_ID = "UNSPECIFIED";
	protected static final String PROCESS_ID_FIELD = "processId";
	protected static final String TAGS_FIELD = "tags";
	protected static final String NO_TAGS = "none";
	protected static final String COMMENT_PREFIX = "#";
	
	protected List<String> clientPaths;
	protected List<ClientThread> clients;
	
	protected ByteArrayOutputStream pausedOutput;
	
	protected AbstractXMPPConnection connection;
	

	public static void main(String[] args) {
		new Central().init();
	}
	
	protected void init() {
		clientPaths = new LinkedList<String>();
		clients = new LinkedList<ClientThread>();
		getClientPaths();
		initClients();
		initXMPP();
	}
	
	protected void getClientPaths() {
		clientPaths.addAll(getClientPathsFromFile());
		clientPaths.addAll(getClientPathsFromFolder());
	}
	
	protected static List<String> getClientPathsFromFile() {
		List<String> clients = new LinkedList<String>();
		File pathsFile = new File(CLIENT_PATHS_FILE);
		try {
			Scanner scanner = new Scanner(pathsFile);
			while(scanner.hasNextLine()) {
				String aNextLine = scanner.nextLine();
				if (aNextLine.startsWith(COMMENT_PREFIX))
					continue;
				clients.add(aNextLine);
//				clients.add(scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Server paths file " + CLIENT_PATHS_FILE + " not found.");
			e.printStackTrace();
		}
		return clients;
		
	}
	
	protected static List<String> getClientPathsFromFolder() {
		List<String> clients = new LinkedList<String>();
		File folder = new File(CLIENT_FOLDER);
		for(String file : folder.list()) {
			String[] tokens = file.split("\\.");
			if(tokens.length >= 2) {
				if(tokens[tokens.length - 1].equalsIgnoreCase("jar")) {
					clients.add(CLIENT_FOLDER + "/" + file);
				}
			}
		}
		return clients;
	}
	
	protected void initClients() {
		for(String jarPath : clientPaths) {
			try {
				initClient(jarPath);
			} catch (Exception ex) {
				System.out.println("Could not load " + jarPath);
				ex.printStackTrace();
			}
		}
	}
	
	protected void initClient(String exePath) throws Exception {
		String fileExtension = exePath.substring(exePath.lastIndexOf(".") + 1);
		ProcessBuilder builder;
		if(fileExtension.equalsIgnoreCase("jar")) {
			builder = new ProcessBuilder("java", "-jar", (new File(exePath)).getAbsolutePath());
		} else if (fileExtension.equalsIgnoreCase("js")) {
			builder = new ProcessBuilder("node", (new File(exePath)).getAbsolutePath());
		} else {
			return;
		}
		
		File directory = (new File(exePath)).getParentFile();
		if (!directory.exists()) {
			System.out.println(directory + " specified in clients.txt or clients folder does not exist, cannot start message bus client");
			return;
		}
		if(directory != null) {
			builder.directory(directory);
		}
		
		try {
			Process clientProcess = builder.start();
			ClientThread client = new ClientThread(exePath, clientProcess, this);
			clients.add(client);
		} catch (Exception ex) {
			System.out.println("Failed to start server " + exePath);
			ex.printStackTrace();
		}
	}
	/**
	 * Gets XMPP credentials, from file it it exists, consle otherwise.
	 * Also keeps reading user input,doing nothing, until q is pressed.
	 * Should actually have start and end mesage so we can interactively
	 * send messages. TO-DO
	 */
	protected void initXMPP() {
		try {
			Scanner credentialsScanner = null;
			Scanner interactiveScanner = new Scanner(System.in);
			File aCredentialsFile = new File(XMPP_CREDENTIALS_FILE);
			if (aCredentialsFile.exists()) {
				credentialsScanner = new Scanner(aCredentialsFile);
			} else {
				credentialsScanner = interactiveScanner;
//				scanner = new Scanner(System.in);
			}
//			Scanner scanner = new Scanner(System.in);
			System.out.print("XMPP Username: ");
			String[] usernameAndDomain = credentialsScanner.next().split("@");
			String username = usernameAndDomain[0];
			String domain = usernameAndDomain[1];
			System.out.print("\nHost: ");
			String host = credentialsScanner.next();
			System.out.print("\nPassword: ");
			String password = credentialsScanner.next();
			System.out.println();
			System.out.print("\nSecurity? ");
			boolean security = credentialsScanner.next().toLowerCase().contains("y");
			System.out.println();
			System.out.println("Connetion parameters\n" +
						" User Name:" + username + 
						" Password:" + password + 
						" Domain:" + domain +
						" Host:" + host +
						" Security Mode: " + security);
	
			XMPPTCPConnectionConfiguration.Builder configBuilder = 
					XMPPTCPConnectionConfiguration.builder();
			configBuilder.setUsernameAndPassword(username, password);
			configBuilder.setServiceName(domain);
			configBuilder.setHost(host);
			if(!security) {
				configBuilder.setSecurityMode(SecurityMode.disabled);
			}
			
			connection = new XMPPTCPConnection(configBuilder.build());
			try {
				connection.connect();
				connection.login();
				connection.addSyncStanzaListener(this, this);
				System.out.println("XMPP Connected.");
			} catch (Exception ex) {
				System.out.println("Failed to login.");
				credentialsScanner.close();
				stopClients();
				ex.printStackTrace();
				return;
			}

//			while(!scanner.next().equals("q"));
//			scanner.close();
			while(!interactiveScanner.next().equals("q"));
			interactiveScanner.close();
			stopClients();
			if (interactiveScanner != credentialsScanner) {
				credentialsScanner.close();
			}
		} catch (Exception ex) {
			System.out.println("Error processing user input");
			ex.printStackTrace();
		}
	}
	
	protected void stopClients() {
		for(ClientThread client : clients) {
			client.stop();
		}
	}
	
	//to be called by clients
	public void receiveMessage(String message) {
		JSONObject json;
		try {
			json = new JSONObject(message);
			receiveMessage(json);
		} catch (Exception ex) {}
		
	}
	
	public void sendMessage(String messageBody) {
		JSONObject messageJSON = new JSONObject(messageBody);
		String to = messageJSON.getString("to");
		if(to == null) {
			return;
		}
		if(connection.isConnected()) {
			Message message = new Message(to);
			message.setBody("0.true." + messageBody);
			try { 
				System.out.println("Sending message: " + message.toString());
				connection.sendStanza(message);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	protected void receiveMessage(JSONObject message) {
		System.out.println(message.toString());
		if(!message.has(PROCESS_ID_FIELD)) {
			message.put(PROCESS_ID_FIELD, UNSPECIFIED_PROCESS_ID);
		}
		forwardMessage(message);
	}
	
	protected void forwardMessage(JSONObject message) {
		JSONArray tags;
		if(!message.has(TAGS_FIELD)) {
			tags = new JSONArray();
			tags.put(NO_TAGS);
		} else {
			tags = (JSONArray) message.get(TAGS_FIELD);
		}
		boolean messageResponder = !message.has("CALLBACK_TAG");
		for(ClientThread client : clients) {
			for(int i = 0; i < tags.length(); i++) {
				if(client.matchesTag(tags.getString(i))) {
					client.receiveMessage(message.toString());
					messageResponder = true;
					break;
				}
			}
		}
		if(!messageResponder) {
			sendNoResponse(message);
		}
	}
	
	protected void sendNoResponse(JSONObject message) {
		JSONArray tags = new JSONArray();
		tags.put(message.getString("CALLBACK_TAG"));
		message.put("RespondedTo", false);
	}
	
	@Override
	public boolean accept(Stanza stanza) {
		return stanza instanceof Message;
	}
	
	@Override
	public void processPacket(Stanza stanza) throws NotConnectedException {
		Message message = (Message) stanza;
		try {
			String messageBody = message.getBody();
			JSONObject json = new JSONObject(messageBody);
			receiveMessage(json);
		} catch (Exception ex) {
			System.out.println(message);
			System.out.println(message.getBody());
			ex.printStackTrace();
		}
	}
	
	protected void pauseOutput() {
		pausedOutput = new ByteArrayOutputStream();
		System.setOut(new PrintStream(pausedOutput));
	}
	
	protected void unpauseOutput() {
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
		System.out.print(pausedOutput.toString());
	}
	
}

package hermes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.lang3.exception.ExceptionUtils;

import util.trace.hermes.messagebus.ClientProcessRegisteredTags;
import util.trace.hermes.messagebus.ClientProcessProducedOutput;

public class ClientThread {
	
	protected static final String TAG_START = "<TAG>";
	protected static final String MSG_START = "<MSG>";
	protected static final String OUTPUT_START = "<OUTPUT>";
	protected static final String MSG_SEND = "<SEND>";
	
	protected String name;
	protected String shortName;
	protected Process process;
	protected StreamThread inputStream;
	protected ErrorStreamThread errorStream;
	protected PrintWriter output;
	protected Pattern tagsPattern;
	protected Central central;
	

	public ClientThread(String name, Process process, Central central) {
		this.name = name;
		shortName = toShortName(name);
		this.process = process;
		this.central = central;
		init();
	}
	public ClientThread(Central aCentral) {		
		this.central = aCentral;
//		init();
	}
	protected String toShortName(String aName) {
		int aSlashIndex = aName.lastIndexOf("/");
		if (aSlashIndex == -1) {
			aSlashIndex = aName.lastIndexOf("\\");
		}
		if (aSlashIndex == -1) {
			return aName;
		}
		return aName.substring(aSlashIndex + 1);
	}
	@Override
	public String toString() {
		return shortName;
	}
	
	protected void init() {
		inputStream = new StreamThread(process.getInputStream());
//		errorStream = new StreamThread(process.getErrorStream());
		errorStream = new ErrorStreamThread(process.getErrorStream());

		output = new PrintWriter(new BufferedOutputStream(process.getOutputStream()));
		(new Thread(inputStream)).start();
		(new Thread(errorStream)).start();
	}
	
	public boolean matchesTag(String tag) {
		return (tagsPattern == null) ? false : tagsPattern.matcher(tag).matches();
	}
	
	protected void setTags(String tagsRegex) {
		tagsPattern = Pattern.compile(tagsRegex);
		ClientProcessRegisteredTags.newCase(this, this, tagsRegex);
	}
	
	public void stop() {
		inputStream.stop();
		errorStream.stop();
		process.destroy();
	}
	
	public void receiveMessage(String message) {
		output.println(message.length());
		output.println(message);
		output.flush();
	}
	
	
	protected class StreamThread implements Runnable {
		
		private InputStream stream;
		private boolean running;
		
		public StreamThread(InputStream stream) {
			this.stream = stream;
			running = true;
		}
		
		public void stop() {
			running = false;
		}
		
		public void run() {
			Scanner scanner = new Scanner(new BufferedInputStream(stream));
			while(running) {
				try {
					if(!scanner.hasNextInt()){
						System.out.println("client output: " + scanner.next());
						continue;
					}
					int lineLength = scanner.nextInt();
					scanner.nextLine();
					String next = "";
					while(lineLength > 0) {
						String nextLine = scanner.nextLine();
						next += nextLine;
						lineLength -= nextLine.length();
						if(lineLength > 0) {
							next += "\n";
							lineLength--;
						}
					}
					if(next.startsWith(TAG_START)) {
						setTags(next.substring(TAG_START.length()));
					} else if (next.startsWith(MSG_START)) {
						central.receiveMessage(next.substring(MSG_START.length()));
					} else if (next.startsWith(OUTPUT_START)){
						String anOutput = next.substring(OUTPUT_START.length());
						ClientProcessProducedOutput.newCase(this, name, anOutput);						
//						System.out.println(name + ": " + next.substring(OUTPUT_START.length()));
						System.out.println(name + ": " + anOutput);

					} else if (next.startsWith(MSG_SEND)) {
						central.sendMessage(next.substring(MSG_SEND.length()));
					}
				} catch (Exception ex) {
					System.out.println(name + ": " + ExceptionUtils.getStackTrace(ex));
					break;
				}
			}
			scanner.close();
		}
		
	}
protected class ErrorStreamThread implements Runnable {
		
		private InputStream stream;
		private boolean running;
		
		public ErrorStreamThread(InputStream stream) {
			this.stream = stream;
			running = true;
		}
		
		public void stop() {
			running = false;
		}
		
		public void run() {
			Scanner scanner = new Scanner(new BufferedInputStream(stream));
			while(running) {
				try {
					String next = scanner.nextLine();
					System.err.println("client err: " + next);

				} catch (Exception ex) {
					System.out.println(name + ": " + ExceptionUtils.getStackTrace(ex));
					break;
				}
			}
			scanner.close();
		}
		
	}
	
}
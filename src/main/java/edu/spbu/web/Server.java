package edu.spbu.web;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

	private final ServerSocket socket;
	private final String hostname;    // = "server.web.spbu.edu"
	private String root = "src/resources/server";

	public Server(String hostname, int port) {
		try {
			this.hostname = hostname;
			this.socket = new ServerSocket(port);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Incorrect port number, use number from 0 to 65536", e);
		} catch (Exception e) {
			throw new RuntimeException("Server start failed, try again", e);
		}
	}

	public Server(String hostname, int port, String root) {
		try {
			this.hostname = hostname;
			this.socket = new ServerSocket(port);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Incorrect port number, use number from 0 to 65536", e);
		} catch (Exception e) {
			throw new RuntimeException("Server start failed, try again", e);
		}
		this.root = root;
	}

	public void run(){
		Server thisServer = this;
		List<Thread> threadList = Collections.synchronizedList(new ArrayList<Thread>());
		Thread server_thread = new Thread("Server Thread") {
			public void run() {
				while (true) {
					try {
						Socket x = thisServer.socket.accept();
						System.out.println("Connection: " + x.getInetAddress());
						Thread thread = new Thread(new RequestResponder(x, thisServer));
						thread.start();
						threadList.add(thread);
					} catch (SocketException se){
						System.out.println("Server stopped");
						return;
					}
					catch (IOException e) {
						throw new RuntimeException("Failed to establish connection with client", e);
					}
				}
			}
		};

		server_thread.start();
		System.out.println("Server started");

		try (Scanner stdIn = new Scanner(System.in)){
			String line = stdIn.nextLine();
			while (true) {
				if (line.equals("stop")){
					for (Thread t : threadList) {
						try {
							t.join();
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}
					this.socket.close();
					return;
				}
				line = stdIn.nextLine();
			}
		} catch (IOException e){
			throw new RuntimeException("Server can't receive messages, please restart it", e);
		}
	}

	String getRoot(){
		return this.root;
	}

	String getHostname(){
		return this.hostname;
	}

}

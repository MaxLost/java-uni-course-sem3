package edu.spbu.web;

import java.io.*;
import java.net.*;
import java.nio.file.*;
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
		Server this_server = this;
		List<Thread> thread_pool = Collections.synchronizedList(new ArrayList<Thread>());
		Thread server_thread = new Thread("Server Thread") {
			public void run() {
				while (true) {
					try {
						Socket x = this_server.socket.accept();
						System.out.println("Connection: " + x.getInetAddress());
						Thread thread = new Thread(new RequestResponder(x, this_server));
						thread.start();
						thread_pool.add(thread);
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

		try (Scanner stdin = new Scanner(System.in)){
			String line = stdin.nextLine();
			while (true) {
				if (line.equals("stop")){
					for (Thread t : thread_pool) {
						try {
							t.join();
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}
					this.socket.close();
					return;
				}
				line = stdin.nextLine();
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

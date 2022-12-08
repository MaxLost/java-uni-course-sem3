package edu.spbu.web;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class Server {

	ServerSocket socket;
	String hostname;    // = "server.web.spbu.edu"
	private String root = "src/resources/server";
	private boolean isRunning;

	public Server(String hostname, int port) {
		try {
			this.hostname = hostname;
			this.socket = new ServerSocket(port);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Incorrect port number, use number from 0 to 65536", e);
		} catch (Exception e) {
			throw new RuntimeException("Server start failed, try again", e);
		}
		isRunning = true;
		System.out.println("Server started");
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
		isRunning = true;
	}

	public void run(){
		ArrayList<Thread> thread_pool = new ArrayList<>();

		class Responder implements Runnable {

			private final Socket connection;

			public Responder(Socket connection){
				this.connection = connection;
			}

			@Override
			public void run(){
				try (PrintWriter output = new PrintWriter(this.connection.getOutputStream());
				     BufferedReader input = new BufferedReader(new InputStreamReader(this.connection.getInputStream()))
				){
					List<String> request = new ArrayList<>();
					String line;
					while (!(line = input.readLine()).equals("")){
						Collections.addAll(request, line.split(" "));
					}

					if (request.get(2).equals("HTTP/1.0") || request.get(2).equals("HTTP/1.1")){
						String protocol = request.get(2);
						if (request.get(4).equals(hostname)) {
							if (request.get(0).equals("GET")) {
								String path = (request.get(1).equals("/")) ? "/index.html" : request.get(1);
								Path requested_file = Paths.get(root + path);

								try (Scanner scanner = new Scanner(requested_file)){
									java.util.Date date = new java.util.Date();
									output.write(protocol + " 200 Ok\nContent-Type: text/html\n" +
											"Content-Length: "+ Files.size(requested_file) +"\nConnection: close\n" +
											"Date: " + date + "\nServer: Custom\n\r\n");
									while (scanner.hasNextLine()){
										output.write(scanner.nextLine());
									}
									output.flush();
									connection.close();
								} catch (IOException e) {
									java.util.Date date = new java.util.Date();
									output.write(protocol + " 404 Not Found\nContent-Type: text/html\n" +
											"Content-Length: 0\nConnection: close\nDate: " + date +
											"\nServer: Custom\n\r\n");
									output.flush();
									connection.close();
								}
							} else {
								java.util.Date date = new java.util.Date();
								output.write(protocol + " 400 Bad Request\nContent-Type: text/html\n" +
										"Content-Length: 0\nConnection: close\nDate: " + date + "\nServer: Custom\n\r\n");
								output.flush();
								connection.close();
							}
						}
					}

				} catch (java.io.IOException e) {
					throw new RuntimeException("Failed to get package!", e);
				}
			}

		}

		while (isRunning) {
			try {
				Socket x = this.socket.accept();
				System.out.println("Connection: " + x.getInetAddress());
				Thread thread = new Thread(new Responder(x));
				thread.start();
				thread_pool.add(thread);
			} catch (IOException e) {
				throw new RuntimeException("Failed to establish connection with client", e);
			}
		}

		for (Thread t : thread_pool){
			try {
				t.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void stop(){
		this.isRunning = false;
	}

	public boolean isRunning(){
		return this.isRunning;
	}

}

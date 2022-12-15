package edu.spbu.web;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Server {

	ServerSocket socket;
	String hostname;    // = "server.web.spbu.edu"
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

			class Responder implements Runnable {

				private final Socket connection;

				public Responder(Socket connection) {
					this.connection = connection;
				}

				@Override
				public void run() {
					try (PrintWriter output = new PrintWriter(this.connection.getOutputStream());
					     BufferedReader input = new BufferedReader(new InputStreamReader(this.connection.getInputStream()))
					) {
						List<String> request = new ArrayList<>();
						String line;
						while ((line = input.readLine()) != null && !line.equals("")) {
							Collections.addAll(request, line.split(" "));
						}

						if (request.size() < 2) {
							sendBadRequestResponse(this.connection);
							connection.close();
							return;
						}

						if (request.get(2).equals("HTTP/1.0") || request.get(2).equals("HTTP/1.1")) {
							String protocol = request.get(2);
							if (request.get(4).equals(hostname)) {
								if (request.get(0).equals("GET")) {
									String path = (request.get(1).equals("/")) ? "/index.html" : request.get(1);

									if (path.equals("")) {
										sendBadRequestResponse(this.connection);
										connection.close();
										return;
									}

									Path requested_file = Paths.get(root + path);

									sendFile(this.connection, protocol, requested_file);

								} else {
									sendBadRequestResponse(this.connection);
									connection.close();
								}
							}
						}
					} catch (java.io.IOException e) {
						throw new RuntimeException("Failed to get or send package", e);
					}
				}

			}
			public void run() {
				while (true) {
					try {
						Socket x = this_server.socket.accept();
						System.out.println("Connection: " + x.getInetAddress());
						Thread thread = new Thread(new Responder(x));
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

	private void sendFileNotFoundResponse(Socket connection, String protocol) {
		try (PrintWriter output = new PrintWriter(connection.getOutputStream())) {
			java.util.Date date = new java.util.Date();
			Path response = Paths.get(root + "/404.html");
			output.write(protocol + " 404 Not Found\nContent-Type: text/html\n" +
					"Content-Length: " + Files.size(response) + "\nConnection: close\nDate: "
					+ date + "\nServer: Custom\n\r\n");
			try (Scanner scanner = new Scanner(response)) {
				while (scanner.hasNextLine()) {
					output.write(scanner.nextLine() + "\n");
				}
			}
			output.flush();
		} catch (IOException e) {
			throw new RuntimeException("Failed to get or send package", e);
		}
	}

	private void sendFile(Socket connection, String protocol, Path requested_file){
		try (PrintWriter output = new PrintWriter(connection.getOutputStream())) {
			try (Scanner scanner = new Scanner(requested_file)) {
				java.util.Date date = new java.util.Date();
				output.write(protocol + " 200 OK\nContent-Type: text/html\n" +
						"Content-Length: " + Files.size(requested_file) + "\nConnection: close\n" +
						"Date: " + date + "\nServer: Custom\n\r\n");
				while (scanner.hasNextLine()) {
					output.write(scanner.nextLine() + "\n");
				}
				output.flush();
				connection.close();
			} catch (NoSuchFileException e) {
				sendFileNotFoundResponse(connection, protocol);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to get or send package", e);
		}
	}

	private void sendBadRequestResponse(Socket connection){

		try (PrintWriter output = new PrintWriter(connection.getOutputStream())) {
			java.util.Date date = new java.util.Date();
			Path response = Paths.get(root + "/400.html");
			output.write("HTTP/1.0 400 Bad Request\nContent-Type: text/html\n" +
					"Content-Length: " + Files.size(response) + "\nConnection: close\nDate: "
					+ date + "\nServer: Custom\n\r\n");
			try (Scanner scanner = new Scanner(response)) {
				while (scanner.hasNextLine()) {
					output.write(scanner.nextLine() + "\n");
				}
			}
			output.flush();
		} catch (IOException e) {
			throw new RuntimeException("Failed to get or send package", e);
		}
	}

}

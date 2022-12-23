package edu.spbu.web;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.util.*;

class RequestResponder implements Runnable {

	private final Server server;

	private final Socket connection;

	public RequestResponder(Socket connection, Server server) {
		this.connection = connection;
		this.server = server;
	}

	@Override
	public void run() {
		try (BufferedReader input = new BufferedReader(new InputStreamReader(this.connection.getInputStream()))) {
			List<String> request = new ArrayList<>();
			String line;
			while ((line = input.readLine()) != null && !line.equals("")) {
				Collections.addAll(request, line.split(" "));
			}

			if (request.size() < 3) {
				sendBadRequestResponse(this.connection);
				connection.close();
				return;
			}

			if (request.get(2).equals("HTTP/1.0") || request.get(2).equals("HTTP/1.1")) {
				String protocol = request.get(2);
				if (request.get(4).equals(server.getHostname())) {
					if (request.get(0).equals("GET")) {
						String path = (request.get(1).equals("/")) ? "/index.html" : request.get(1);

						if (path.equals("")) {
							sendBadRequestResponse(this.connection);
							return;
						}

						try {
							Path requested_file = Paths.get(server.getRoot() + path);
							if (!Files.exists(requested_file) || !Files.isRegularFile(requested_file)){
								throw new InvalidPathException(requested_file.toString(), "File cannot be sent");
							}
							sendFile(this.connection, protocol, requested_file);
						}
						catch (InvalidPathException e){
							sendFileNotFoundResponse(this.connection, protocol);
						}

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

	private void sendFileNotFoundResponse(Socket connection, String protocol) {
		try {
			byte[] file = Files.readAllBytes(Paths.get(server.getRoot() + "/404.html"));
			respond(connection, protocol, "404 File Not Found", file);
			connection.close();
		} catch (IOException e) {
			respond(connection, protocol, "404 File Not Found", new byte[0]);
			throw new RuntimeException("Failed to read requested file", e);
		}
	}

	private void sendFile(Socket connection, String protocol, Path requested_file) {
		try {
			byte[] file = Files.readAllBytes(requested_file);
			respond(connection, protocol, "200 OK", file);
		} catch (IOException e){
			throw new RuntimeException("Failed to read requested file", e);
		}
	}

	private void sendBadRequestResponse(Socket connection){

		try {
			byte[] file = Files.readAllBytes(Paths.get(server.getRoot() + "/400.html"));
			respond(connection, "HTTP/1.0", "400 Bad Request", file);

		} catch (IOException e) {
			respond(connection, "HTTP/1.0", "400 Bad Request", new byte[0]);
			throw new RuntimeException("Failed to read file", e);
		}
	}

	private void respond(Socket connection, String protocol, String status, byte[] message){
		try (PrintWriter output = new PrintWriter(connection.getOutputStream())){
			java.util.Date date = new java.util.Date();
			output.write(protocol + " " + status + "\nContent-Type: text/html\n" +
					"Content-Length: " + message.length + "\nConnection: close\n" +
					"Date: " + date + "\nServer: Custom\n\r\n");
			for (byte x : message){
				output.write(x);
			}
			output.flush();
		} catch (IOException e) {
			throw new RuntimeException("Failed to send package", e);
		}
	}

}

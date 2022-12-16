package edu.spbu.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

class RequestResponder implements Runnable {

	private final Server server;

	private final Socket connection;

	public RequestResponder(Socket connection, Server server) {
		this.connection = connection;
		this.server = server;
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
				if (request.get(4).equals(server.getHostname())) {
					if (request.get(0).equals("GET")) {
						String path = (request.get(1).equals("/")) ? "/index.html" : request.get(1);

						if (path.equals("")) {
							sendBadRequestResponse(this.connection);
							connection.close();
							return;
						}

						Path requested_file = Paths.get(server.getRoot() + path);

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

	private void sendFileNotFoundResponse(Socket connection, String protocol) {
		try (PrintWriter output = new PrintWriter(connection.getOutputStream())) {
			java.util.Date date = new java.util.Date();
			Path response = Paths.get(server.getRoot() + "/404.html");
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
			Path response = Paths.get(server.getRoot() + "/400.html");
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

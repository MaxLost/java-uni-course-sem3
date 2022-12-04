package edu.spbu.web;

import java.io.*;
import java.net.*;

public class Client {

	InetAddress target;
	int port;

	public Client(String address, int port){
		try {
			this.target = InetAddress.getByName(address);
			this.port = port;
		} catch (java.net.UnknownHostException e) {
			throw new RuntimeException("Unknown host, please check hostname!", e);
		}
	}

	public void get(String request){
		try (Socket connection = new Socket(target, this.port)) {
			System.out.println("Connection established");

			try (PrintWriter output = new PrintWriter(connection.getOutputStream());
			     BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()))
			) {
				output.println(request);
				output.flush();
				System.out.println("ECHO: \n");
				String line;
				while ((line = input.readLine()) != null) {
					System.out.println(line);
				}
			} catch (java.io.IOException e) {
				throw new RuntimeException("Failed to get package!", e);
			}
		} catch (java.io.IOException e) {
			throw new RuntimeException("Connection failed!", e);
		}

	}

}

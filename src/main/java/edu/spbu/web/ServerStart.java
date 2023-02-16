package edu.spbu.web;

public class ServerStart {
	public static void main(String[] args) {
		Server port80 = new Server("127.0.0.1", 80);
		port80.run();
	}
}

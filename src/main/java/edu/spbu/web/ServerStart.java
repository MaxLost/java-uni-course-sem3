package edu.spbu.web;

public class ServerStart {
	public static void main(String[] args) {
		Server x = new Server("127.0.0.1", 80);
		x.run();
	}
}

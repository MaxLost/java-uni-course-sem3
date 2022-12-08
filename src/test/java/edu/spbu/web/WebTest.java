package edu.spbu.web;

import org.junit.Test;
import static org.junit.Assert.*;

public class WebTest {

	@Test
	public void ClientTest(){
		Client x = new Client("example.com", 80);
		x.get("GET  HTTP/1.1\nHost: example.com\n\r\n");
	}

	@Test
	public void ServerRun(){
		Server x = new Server("server.web.spbu.edu", 80);
		x.run();
	}
}

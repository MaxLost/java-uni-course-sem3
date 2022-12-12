package edu.spbu.web;

import org.junit.Test;
import static org.junit.Assert.*;

public class WebTest {

	@Test
	public void ClientTest(){
		Client x = new Client("127.0.0.1", 80);
		x.get("GET / HTTP/1.1\nHost: 127.0.0.1\n\r\n");
	}

	@Test
	public void ServerRun(){

	}
}

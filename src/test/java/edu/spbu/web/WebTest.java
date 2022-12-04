package edu.spbu.web;

import org.junit.Test;
import static org.junit.Assert.*;

public class WebTest {

	@Test
	public void ClientTest(){
		Client x = new Client("example.com", 80);
		x.get("GET /index.html HTTP/1.1\nHost: example.com\n\r\n");
	}
}

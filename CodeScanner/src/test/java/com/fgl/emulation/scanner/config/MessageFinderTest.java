package com.fgl.emulation.scanner.config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MessageFinderTest {
	private MessageFinder messageFinder;
	
	@Before
	public void setupTest() {
		messageFinder = new MessageFinder();
		assertNotNull(messageFinder);
	}
	
	@After
	public void finishTest() {
		messageFinder = null;
		assertNull(messageFinder);
	}	
	
	@Test
	public void testFind() {
		messageFinder.setLocale("","");
		messageFinder.loadFile("messages");		
		String value = messageFinder.find("no_cam");
		assertNotNull(value);
	}

}

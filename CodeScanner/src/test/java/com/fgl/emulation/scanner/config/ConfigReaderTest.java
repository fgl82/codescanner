package com.fgl.emulation.scanner.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigReaderTest {

	private ConfigReader configReader;
	
	@Before
	public void setupTest() {
		configReader = new ConfigReader();
		assertNotNull(configReader);
	}
	
	@After
	public void finishTest() {
		configReader = null;
		assertNull(configReader);
	}	
	
	@Test
	public void testLoadFileAndGetValue() {
		boolean noError = true;
		String value=null;
		try {
			configReader.loadFile("cfg/nofront.properties");
			value = configReader.getValue("nesExec");
		} catch (IOException e) {
			noError = false;
		}
		assertTrue(noError);
		assertNotNull(value);
		try {
			configReader.loadFile("cfg/nofront.propertie");
		} catch (IOException e) {
			noError = false;
		}
		assertFalse(noError);		
	}	
}

package com.fgl.emulation.scanner.capture;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CodeReaderTest {
	
	CamQRCodeReader codeReader;
	
	@Before
	public void setupTest() {
		codeReader = new CamQRCodeReader();
		assertNotNull(codeReader);
	}
	
	@After
	public void finishTest() {
		codeReader.finish();
		assertFalse(codeReader.isReading());
		codeReader = null;		
		assertNull(codeReader);
	}	
	
	@Test
	public void testRead() throws IOException {
		codeReader.initialize();
		assertTrue(codeReader.isReading());
		boolean codeReadError = false;
		try {			
			codeReader.read(true,"C:\\Users\\Administrador.000\\git\\codescanner\\CodeScanner\\codes\\snes\\ActRaiser (USA).png");
			codeReader.read(false);
		} catch (CodeReadException e1) {
			codeReadError = true;
		} 
		assertNotNull(codeReadError);
	}

}

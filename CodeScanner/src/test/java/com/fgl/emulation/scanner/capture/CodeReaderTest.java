package com.fgl.emulation.scanner.capture;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;

public class CodeReaderTest {
	
	CodeReader codeReader;
	
	@Before
	public void setupTest() {
		codeReader = new CodeReader();
		assertNotNull(codeReader);
	}
	
	@After
	public void finishTest() {
		codeReader.close();
		assertFalse(codeReader.isReading());
		codeReader = null;		
		assertNull(codeReader);
	}	
	
	@Test
	public void testRead() throws IOException {
		codeReader.open();
		assertTrue(codeReader.isReading());
		boolean formatError = false;
		boolean checksumError = false;
		boolean readError = false;
		try {			
			codeReader.read(true,"C:\\Users\\Administrador.000\\git\\codescanner\\CodeScanner\\codes\\snes\\ActRaiser (USA).png");
			codeReader.read(false);
		} catch (FormatException e1) {
			formatError = true;
		} catch (NotFoundException e) {
			readError=true;
		} catch (ChecksumException e) {
			checksumError=true;
		}
		assertNotNull(readError);
		assertFalse(formatError);	
		assertFalse(checksumError);
	}

}

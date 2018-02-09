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
		codeReader.open();
		assertNotNull(codeReader);
		assertTrue(codeReader.isReading());
	}
	
	@After
	public void finishTest() {
		codeReader.close();
		assertFalse(codeReader.isReading());
		codeReader = null;		
		assertNull(codeReader);
	}	
	
	@Test
	public void testRead() {
		boolean readError = false;
		boolean formatError = false;
		boolean checksumError = false;
		boolean camError = false;
		try {			
			codeReader.read();
		} catch (IOException e) {
			readError = true;
		} catch (FormatException e1) {
			formatError = true;
		} catch (NotFoundException e) {
			camError=true;
		} catch (ChecksumException e) {
			checksumError=true;
		}
		assertNotNull(camError);
		assertFalse(readError);
		assertFalse(formatError);		
		assertFalse(checksumError);
	}

}
package scanner;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.fgl.emulation.scanner.main.NoFront;

public class NoFrontTest {
	@Test
	public void testNew() {
		NoFront nofront = new NoFront();
		assertNotNull(nofront);
	}
	
//	@Test
//	public void testFileExistsinDir() {		
//		assertFalse(NoFront.fileExistsInDir("pepe", "C:\\Users\\Fernando\\SkyDrive\\Development\\CodeScanner\\snes"));	
//		assertTrue(NoFront.fileExistsInDir("ActRaiser (USA).png", "C:\\Users\\Fernando\\SkyDrive\\Development\\CodeScanner\\snes"));
//	}
//
//	@Test
//	public void testLaunch() {
//		try {
//			NoFront nofront = new NoFront();
//			nofront.loadProperties("cfg/nofront.properties");
//			assertFalse(nofront.launch("pepe"));
//		} catch (IOException e) {			 
//			
//		}
//	}
}

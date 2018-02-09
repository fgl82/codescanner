package com.fgl.emulation.scanner.launch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GameLauncherTest {
	GameLauncher gameLauncher;
	
	@Before
	public void setupTest() throws IOException {
		gameLauncher=new GameLauncher();
		assertNotNull(gameLauncher);
	}
	
	@After
	public void finishTest() throws IOException {
		gameLauncher=null;
		assertNull(gameLauncher);
	}
	
	@Test
	public void testLaunch() throws IOException {
		try {
			gameLauncher.addFolderAndExec("C:/EmuStation/roms/nes","\"C:/EmuStation/Retroarch/retroarch.exe\" -L \"C:/EmuStation/Retroarch/cores/nestopia_libretro.dll\"");
			assertFalse(gameLauncher.launch("Arkanoid (USA.zip"));
			assertTrue(gameLauncher.launch("Arkanoid (USA).zip"));
		} catch (IOException e) {
			throw e;
		}
	}
	
	@Test
	public void testAddAndGetFoldersAndExec() throws IOException {
		gameLauncher.addFolderAndExec("someFolder","someExec");
		String exec = gameLauncher.getFoldersAndExecs().get("someFolder");
		assertTrue("someExec".equals(exec));
	}	
}

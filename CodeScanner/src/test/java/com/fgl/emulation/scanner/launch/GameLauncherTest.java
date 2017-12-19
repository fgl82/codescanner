package com.fgl.emulation.scanner.launch;

import java.io.IOException;

import org.junit.Test;

public class GameLauncherTest {
	@Test
	public void testLaunch() {
		GameLauncher gameLauncher = new GameLauncher();
		try {
			gameLauncher.launch("sadsa");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

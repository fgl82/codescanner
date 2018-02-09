package com.fgl.emulation.scanner.main;

import org.jnativehook.NativeHookException;
import org.junit.Test;

public class NoFrontTest {
	@Test
	public void testMain() throws NativeHookException {
		NoFront.main(new String[]{});
	}	
}

package com.fgl.emulation.scanner.launch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class KeyListenerTest {
	KeyListener keyListener;
	
	@Before
	public void setupTest() {
		keyListener = new KeyListener();
		assertNotNull(keyListener);
	}
	
	@After
	public void finishTest() {
		keyListener = null;
		assertNull(keyListener);
	}	
	
	@Test
	public void testLaunchComboPath() {
		NativeKeyEvent e = new NativeKeyEvent(2401,10,76,38,(char)65535,1);
		assertFalse(keyListener.isLaunchComboPressed());
		keyListener.nativeKeyPressed(e);
		assertTrue(keyListener.isLaunchComboPressed());		
		keyListener.nativeKeyReleased(e);
		assertFalse(keyListener.isLaunchComboPressed());		
	}

	@Test
	public void testExitComboPath() {
		NativeKeyEvent e = new NativeKeyEvent(2401,10,88,45,(char)65535,1);
		assertFalse(keyListener.isExitComboPressed());
		keyListener.nativeKeyPressed(e);
		assertTrue(keyListener.isExitComboPressed());
		keyListener.nativeKeyReleased(e);
		assertFalse(keyListener.isExitComboPressed());
	}

	@Test
	public void testKeyTyped() {
		NativeKeyEvent e = new NativeKeyEvent(2401,10,88,45,(char)65535,1);
		keyListener.nativeKeyTyped(e);
		assertTrue(true);
	}
}

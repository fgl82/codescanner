package com.fgl.emulation.scanner.launch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class KeyListenerTest {
	KeyListener keyListener;
	
	@Before
	public void setupTest() throws NativeHookException {
		keyListener = new KeyListener();
		assertNotNull(keyListener);
	}
	
	@After
	public void finishTest() throws NativeHookException {
		keyListener.unregister();
		keyListener = null;
		assertNull(keyListener);
	}	
	
	@Test
	public void testLaunchComboPath() {
		//good
		NativeKeyEvent e0 = new NativeKeyEvent(2401,10,76,38,(char)65535,1);
		//ctrl and alt not pressed and letter good
		NativeKeyEvent e1 = new NativeKeyEvent(2401,0,76,38,(char)65535,1);
		//alt not pressed and letter good
		NativeKeyEvent e2 = new NativeKeyEvent(2401,8,76,38,(char)65535,1);
		//ctrl not pressed and letter good
		NativeKeyEvent e3 = new NativeKeyEvent(2401,2,76,38,(char)65535,1);
		//good modifs and wrong letter
		NativeKeyEvent e4 = new NativeKeyEvent(2401,10,76,39,(char)65535,1);
		//all wrong
		NativeKeyEvent e5 = new NativeKeyEvent(2401,0,72,32,(char)65535,1);
		//ctrl not pressed and letter wrong
		NativeKeyEvent e6 = new NativeKeyEvent(2401,2,76,39,(char)65535,1);		
		//alt not pressed and letter wrong
		NativeKeyEvent e7 = new NativeKeyEvent(2401,8,76,39,(char)65535,1);		

		//start good path
		//initially not pressed
		assertFalse(keyListener.isLaunchComboPressed());
		//initially press good
		keyListener.nativeKeyPressed(e0);
		//should be pressed now
		assertTrue(keyListener.isLaunchComboPressed());
		//release
		keyListener.nativeKeyReleased(e0);
		//not pressed now
		assertFalse(keyListener.isLaunchComboPressed());
		//end good path
		
		//press good but modif 0
		keyListener.nativeKeyPressed(e1);	
		assertFalse(keyListener.isLaunchComboPressed());

		//press good but no ctrl
		keyListener.nativeKeyPressed(e2);	
		assertFalse(keyListener.isLaunchComboPressed());		

		//press good but no alt
		keyListener.nativeKeyPressed(e3);	
		assertFalse(keyListener.isLaunchComboPressed());			

		//press good modifs wrong letter
		keyListener.nativeKeyPressed(e4);	
		assertFalse(keyListener.isLaunchComboPressed());			
		
		//press all wrong
		keyListener.nativeKeyPressed(e5);	
		assertFalse(keyListener.isLaunchComboPressed());
		
		//press all wrong
		keyListener.nativeKeyPressed(e6);	
		assertFalse(keyListener.isLaunchComboPressed());		

		//press all wrong
		keyListener.nativeKeyPressed(e7);	
		assertFalse(keyListener.isLaunchComboPressed());	
		
		NativeKeyEvent e8 = new NativeKeyEvent(-121321312,-1232132132,-1232321,-1,(char)-999898981,-17789879);		
		keyListener.nativeKeyPressed(e8);	

		
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

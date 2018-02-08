package com.fgl.emulation.scanner.launch;

import java.util.logging.Handler;
import java.util.logging.Level;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.fgl.emulation.scanner.logging.MyLogger;

public class KeyListener implements NativeKeyListener {
	private boolean launchComboPressed=false;
	private boolean exitComboPressed=false;

	public KeyListener() {
		try {
			hushLogging();
			GlobalScreen.registerNativeHook();
			GlobalScreen.addNativeKeyListener(this);			
		}
		catch (NativeHookException ex) {
			System.exit(1);
		}
	}

	private void hushLogging() {
		java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		Handler[] handlers = java.util.logging.Logger.getLogger("").getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].setLevel(Level.OFF);
		}
	}
	
	public void nativeKeyPressed(NativeKeyEvent e) {		
		boolean isAltPressed = (e.getModifiers() & NativeKeyEvent.ALT_MASK) > 0;
		boolean isCtrlPressed = (e.getModifiers() & NativeKeyEvent.CTRL_MASK) > 0;
		if (isCtrlPressed&&isAltPressed&&NativeKeyEvent.VC_X==e.getKeyCode()) {
			try {
				GlobalScreen.unregisterNativeHook();
				exitComboPressed=true;
			} catch (NativeHookException e1) {
				MyLogger.getLogger().error("Error occurred while unregistering native hook");
			}
		}
		if (isCtrlPressed&&isAltPressed&&NativeKeyEvent.VC_L==e.getKeyCode()) {			
			launchComboPressed=true;
		}
	}

	public void nativeKeyReleased(NativeKeyEvent e) {
		launchComboPressed=false;
	}

	public void nativeKeyTyped(NativeKeyEvent e) {
		//I don't care about this
	}	
	
	public boolean isExitComboPressed() {
		return exitComboPressed;
	}

	public boolean isLaunchComboPressed () {
		return launchComboPressed;
	}	
}

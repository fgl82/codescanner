package com.fgl.emulation.scanner.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fgl.emulation.scanner.main.NoFront;

public class MyLogger {
	private Logger realLogger;
	private static MyLogger me; 
	
	public static MyLogger getLogger() {
		if(me==null) {
			return new MyLogger();
		}
		return me;
	}	
	
	private MyLogger() {
		realLogger = LoggerFactory.getLogger(NoFront.class);
	}
	
	public void info(String string, Object... param) {
		realLogger.info(string,param);
	}
	
	public void error(String string, Object... param) {
		realLogger.error(string,param);
	}
	
}

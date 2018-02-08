package com.fgl.emulation.scanner.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
	
	private Properties properties;
		
	public void loadFile(String fileName) throws IOException {
		properties = new Properties();
		try (InputStream input = new FileInputStream(fileName);) {			
			properties.load(input);
		}
	}	
	
	public String getValue(String property) {
		return properties.getProperty(property);
	}
}

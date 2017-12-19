package com.fgl.emulation.scanner.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public class PropertyReader {
	
	private Properties properties;
		
	public void loadFile(String fileName) throws IOException {
		properties = new Properties();
		try (InputStream input = new FileInputStream(fileName);) {			
			properties.load(input);
		}
	}
	
	public Locale getLocale() {
		String language = properties.getProperty("language");
		String country = properties.getProperty("country");
		if (language==null||country==null) {
			language="";
			country="";
		}
		return new Locale(language,country);
	}
	
	public String getProperty(String property) {
		return properties.getProperty(property);
	}
}

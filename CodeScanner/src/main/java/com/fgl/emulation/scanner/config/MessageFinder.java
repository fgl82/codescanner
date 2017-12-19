package com.fgl.emulation.scanner.config;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageFinder {
	private ResourceBundle messages;
	private Locale locale;
	
	public void setLocale(String country, String language) {
		this.locale = new Locale(country,language);
	}
	
	public void loadFile(String file) {
		messages = ResourceBundle.getBundle(file, locale);
	}
	
	public String find (String message) {
		return messages.getString(message);
	}
	
}

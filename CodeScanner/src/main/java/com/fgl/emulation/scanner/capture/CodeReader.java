package com.fgl.emulation.scanner.capture;

public interface CodeReader {
	public String read(boolean mockRead,String... path) throws CodeReadException;

	public boolean isReading();

	public void open();

	public void close();
}

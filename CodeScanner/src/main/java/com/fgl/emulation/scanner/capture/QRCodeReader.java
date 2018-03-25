package com.fgl.emulation.scanner.capture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.github.sarxos.webcam.Webcam;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class QRCodeReader implements CodeReader{
	private Webcam webcam;

	public String read(boolean mockRead,String... path) throws CodeReadException {
		BufferedImage image = webcam.getImage();
		try {
			if (mockRead) {
				image = ImageIO.read(new File(path[0]));
			}
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
			Reader reader = new MultiFormatReader();
			return reader.decode(bitmap).getText();
		} catch (NotFoundException | ChecksumException | FormatException  | IOException e) {
			throw new CodeReadException(e.getMessage());
		}
	}
		
	public boolean isReading() {
		return (webcam.isOpen());
	}

	public void open() {
		webcam = Webcam.getDefault();
		webcam.open();
	}

	public void close() {
		webcam.close();
	}
}

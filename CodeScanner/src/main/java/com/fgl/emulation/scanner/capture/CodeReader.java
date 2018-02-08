package com.fgl.emulation.scanner.capture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.fgl.emulation.scanner.logging.MyLogger;
import com.github.sarxos.webcam.Webcam;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class CodeReader {
	private Webcam webcam;

	public CodeReader() {
		this.webcam = Webcam.getDefault();
	}

	private Result realRead() throws NotFoundException, ChecksumException, FormatException {
		BufferedImage image = webcam.getImage();
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Reader reader = new MultiFormatReader();
		return reader.decode(bitmap);
	}

	private Result mockRead() throws IOException, NotFoundException, ChecksumException, FormatException {
		String mockImg = ".\\codes\\genesis\\Alien Soldier (Europe).png";
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map < DecodeHintType, Object > tmpHintsMap = new EnumMap (DecodeHintType.class);		
		tmpHintsMap.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);
		tmpHintsMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		BufferedImage image = ImageIO.read(new File(mockImg));
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Reader reader = new MultiFormatReader();
		return reader.decode(bitmap,tmpHintsMap);
	}		

	public String read() throws NotFoundException, ChecksumException, FormatException, IOException {
		if(webcam==null) {
			return mockRead().getText();
		} else {
			return realRead().getText();
		}
	}

	public boolean isReading() {
		return (webcam!=null && webcam.isOpen());
	}

	public boolean open() {
		if (webcam!=null) {
			webcam.open();
		} else {
			return false;
		}
		return true;
	}
	
	public void close() {
		webcam.close();
	}
}

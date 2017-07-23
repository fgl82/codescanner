package scanner;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class NoFront {
	private static Logger logger=LoggerFactory.getLogger("");
	private static Properties properties = loadProperties("cfg/nofront.properties");
	
	public static void main(String[] args){	
		//Initialize everything	
		String rom = "";
		Webcam webcam = Webcam.getDefault();
		int count=0;
		int retries = Integer.parseInt(properties.getProperty("retries"));
		logger.info("Starting the webcam");
		if (!startWebCam(webcam)) {
			logger.info("No Webcam has been detected");
			if (args.length==0||!args[0].equals("letmetry")) {
				count=retries;
			}
		} else {
			logger.info("Webcam started");
			logger.info("Trying to read a code");
			showMessage();			
		}
		while (count<retries) {
			try {			
				//Take picture and try to read it. Every time it fails it goes to the catch block.
				rom = getCodeFromPicture(webcam);
				logger.info("Launching {}",rom);
				launch(rom);
				break;
			} catch (NotFoundException|FormatException|ChecksumException e) {
				if (count==0) {
					logger.info("A code couldn't be read, trying again {} more times",retries);
				}
				count++;
			} catch (Exception e) {
				logger.error(e.toString());
				System.exit(1);
			}
		}
		if (webcam!=null && webcam.isOpen()) {
			logger.info("Closing webcam");			
			webcam.close();
			logger.info("Webcam closed");			
		}
		logger.info("Exiting");
		logger.info("----------");		
		System.exit(0);
	}
	
	public static void launch(String rom) throws IOException {
		String nesRomsDir =  properties.getProperty("nesRomsDir");
		String nesExec = properties.getProperty("nesExec");
		String snesRomsDir = properties.getProperty("snesRomsDir");
		String snesExec = properties.getProperty("snesExec");		
		String genesisRomsDir = properties.getProperty("genesisRomsDir");	
		String genesisExec = properties.getProperty("genesisExec");		
		if (fileExistsInDir(rom,nesRomsDir)) {
			Runtime.getRuntime().exec(nesExec+" \""+nesRomsDir+File.separator+rom+"\"");
		} else if (fileExistsInDir(rom,snesRomsDir)) {
			Runtime.getRuntime().exec(snesExec+" \""+snesRomsDir+File.separator+rom+"\"");
		} else if (fileExistsInDir(rom,genesisRomsDir)) {
			Runtime.getRuntime().exec(genesisExec+" \""+genesisRomsDir+File.separator+rom+"\"");
		} else {
			logger.info("{} can't be found", rom);
		}

	}
	
	public static boolean fileExistsInDir(String file,String dir) {
		return new File(dir,file).exists();
	}

	public static Result mockRead() throws IOException, NotFoundException, ChecksumException, FormatException {
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

	public static Result realRead(Webcam webcam) throws NotFoundException, ChecksumException, FormatException {
		BufferedImage image = webcam.getImage();
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Reader reader = new MultiFormatReader();
		return reader.decode(bitmap);
	}

	public static void showMessage() {
		JFrame frame = new JFrame("CodeScanner");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel label = new JLabel("Scanning");
		frame.getContentPane().add(label, BorderLayout.CENTER);
		frame.setUndecorated(true);
		frame.setBackground(Color.BLACK);
		frame.pack();
		frame.setSize(160,90);
		label.setSize(frame.getWidth(),frame.getHeight());
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
		label.setForeground(Color.GREEN);
		label.setBackground(Color.BLACK);
		label.setOpaque(true);
		Border border = BorderFactory.createLineBorder(Color.GREEN, 4);
		label.setBorder(border);	
		frame.setLocationRelativeTo(null);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
		frame.setVisible(true);
	}

	public static Properties loadProperties(String fileName) {
		Properties props = new Properties();
		try (InputStream input = new FileInputStream(fileName);) {			
			props.load(input);
		} catch (IOException ex) {
			logger.info(ex.toString());
		} 
		return props;
	}

	public static boolean startWebCam(Webcam webcam) {
		if (webcam!=null) {
			webcam.open();
		} else {
			return false;
		}
		return true;
	}

	public static String getCodeFromPicture(Webcam webcam) throws NotFoundException, ChecksumException, FormatException, IOException {
		if(webcam==null) {
			return mockRead().getText();
		} else {
			return realRead(webcam).getText();
		}
	}	
}
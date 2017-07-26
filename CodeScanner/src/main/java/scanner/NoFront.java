package scanner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

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
	private static Logger logger;
	private static Properties properties;
	private static ResourceBundle messages;
	private static Webcam webcam;
	private static boolean processing = false;
	
	public static void main(String[] args) {	
		webcam = Webcam.getDefault();
		properties = loadProperties("cfg/nofront.properties");
		logger = LoggerFactory.getLogger(NoFront.class);
		messages = ResourceBundle.getBundle("messages", getLocale());
		if (Boolean.parseBoolean(properties.getProperty("silent"))) {
			process();
		} else {
			createWindow();	
		}
	}

	private static synchronized void process() {
		processing = true;
		String rom = "";		
		int count=0;
		int retries = Integer.parseInt(properties.getProperty("retries"));		
		logger.info(messages.getString("starting_cam"));
		if (!startWebCam()) {
			logger.info(messages.getString("no_cam"));
			JOptionPane.showMessageDialog(null, messages.getString("no_cam"));
			processing = false;
			return;
		}
		Boolean problemOrEnded = false;
		while (count<retries) {
			try {			
				//Take picture and try to read it. Every time it fails it goes to the catch block.
				rom = getCodeFromPicture();
				logger.info(messages.getString("launching_rom"),rom);
				if (!launch(rom)) {
					JOptionPane.showMessageDialog(null, messages.getString("rom_not_found").replaceAll("{}", rom));
				}
				problemOrEnded=true;
			} catch (NotFoundException|FormatException|ChecksumException e) {
				if (count==0) {
					logger.info(messages.getString("read_fail"),retries);
				}
				count++;
			} catch (Exception e) {
				logger.error(e.toString());
				JOptionPane.showMessageDialog(null, e.toString());
				problemOrEnded=true;
			}
			if (problemOrEnded) {
				break;
			}
		}
		if (webcam!=null && webcam.isOpen()) {
			logger.info(messages.getString("closing_cam"));			
			webcam.close();
			logger.info(messages.getString("cam_closed"));			
		}
		logger.info("----------");
		processing = false;
	}

	private static Properties loadProperties(String fileName) {
		Properties props = new Properties();
		try (InputStream input = new FileInputStream(fileName);) {			
			props.load(input);
		} catch (IOException ex) {
			logger.info(ex.toString());
			JOptionPane.showMessageDialog(null, ex.toString());
			System.exit(1);
		} 
		return props;
	}

	private static Locale getLocale() {
		String language = properties.getProperty("language");
		String country = properties.getProperty("country");
		if (language==null||country==null) {
			language="";
			country="";
		}
		return new Locale(language,country);
	}	

	private static boolean startWebCam() {
		if (webcam!=null) {
			webcam.open();
		} else {
			return false;
		}
		return true;
	}

	private static void createWindow() {
	    class LabelForGIFListener extends MouseAdapter {

	        private final JDialog frame;
	        private final JLabel label;
	        private Point mouseDownCompCoords = null;

	        public LabelForGIFListener(JDialog frame, JLabel label) {
	            this.frame = frame;
	            this.label=label;
	        }

	        @Override
	        public void mouseReleased(MouseEvent e) {
	            mouseDownCompCoords = null;
	        }

	        @Override
	        public void mousePressed(MouseEvent e) {
	            mouseDownCompCoords = e.getPoint();
	        }

	        @Override
	        public void mouseDragged(MouseEvent e) {
	            Point currCoords = e.getLocationOnScreen();
	            frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
	        }
	        
			@Override
			public void mouseClicked(MouseEvent e) {			
				if (!processing) {
					Runnable scanTask = () -> {						
						label.setIcon(new ImageIcon("img/"+properties.getProperty("movingImage")));						
						process();
						label.setIcon(new ImageIcon("img/"+properties.getProperty("staticImage")));
					};
					new Thread(scanTask).start(); 
				}
			}             
	    }			
		JDialog mainWindow = new JDialog();
		JLabel labelForGIF = new JLabel(new ImageIcon("img/"+properties.getProperty("staticImage")));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = screenSize.getWidth();
		double screenHeight = screenSize.getHeight();		
		labelForGIF.setOpaque(false);              
		LabelForGIFListener frameDragListener = new LabelForGIFListener(mainWindow,labelForGIF);
        labelForGIF.addMouseListener(frameDragListener);
        labelForGIF.addMouseMotionListener(frameDragListener);	
		mainWindow.getContentPane().add(labelForGIF, BorderLayout.CENTER);
		mainWindow.setUndecorated(true);
		mainWindow.setBackground(new Color(1.0f,1.0f,1.0f,0.0f));
		mainWindow.getContentPane().setBackground(new Color(1.0f,1.0f,1.0f,0.0f));		
		mainWindow.pack();
		mainWindow.setSize(labelForGIF.getSize());
//		mainWindow.setLocationRelativeTo(null)
		mainWindow.setLocation(((int)screenWidth/2-mainWindow.getWidth()/2), ((int)screenHeight-mainWindow.getHeight()-40));
		mainWindow.setIconImage(Toolkit.getDefaultToolkit().getImage("img/icon.png"));
		mainWindow.setVisible(true);
		mainWindow.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), "exit");
		mainWindow.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), "launch");
		mainWindow.getRootPane().getActionMap().put("exit", new AbstractAction() {
			private static final long serialVersionUID = 2893748791670397467L;
			@Override
		     public void actionPerformed(ActionEvent e) {
			    mainWindow.dispose();
			    System.exit(0);
		     }
		});
		mainWindow.getRootPane().getActionMap().put("launch", new AbstractAction() {
			private static final long serialVersionUID = 5463115862508920904L;
			@Override
		     public void actionPerformed(ActionEvent e) {
				if (!processing) {
					Runnable scanTask = () -> {						
						labelForGIF.setIcon(new ImageIcon("img/"+properties.getProperty("movingImage")));						
						process();
						labelForGIF.setIcon(new ImageIcon("img/"+properties.getProperty("staticImage")));
					};
					new Thread(scanTask).start(); 
				}				
		     }
		});		
		mainWindow.setAlwaysOnTop(Boolean.parseBoolean(properties.getProperty("alwaysOnTop")));
	}

	private static String getCodeFromPicture() throws NotFoundException, ChecksumException, FormatException, IOException {
		if(webcam==null) {
			return mockRead().getText();
		} else {
			return realRead().getText();
		}
	}	

	private static Result realRead() throws NotFoundException, ChecksumException, FormatException {
		BufferedImage image = webcam.getImage();
		LuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Reader reader = new MultiFormatReader();
		return reader.decode(bitmap);
	}

	private static Result mockRead() throws IOException, NotFoundException, ChecksumException, FormatException {
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

	private static boolean launch(String rom) throws IOException {
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
			logger.info(messages.getString("rom_not_found"), rom);
			return false;
		}
		return true;
	}

	private static boolean fileExistsInDir(String file,String dir) {
		return new File(dir,file).exists();
	}
}
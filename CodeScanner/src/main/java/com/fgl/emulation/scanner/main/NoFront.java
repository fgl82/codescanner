package com.fgl.emulation.scanner.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fgl.emulation.scanner.capture.CodeReader;
import com.fgl.emulation.scanner.config.MessageFinder;
import com.fgl.emulation.scanner.config.PropertyReader;
import com.fgl.emulation.scanner.launch.GameLauncher;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;

public class NoFront {
	private static Logger logger = LoggerFactory.getLogger(NoFront.class);
	private static CodeReader qrCodeReader = new CodeReader();
	private static GameLauncher gameLauncher = new GameLauncher();
	private static MessageFinder messageFinder = new MessageFinder();
	private static PropertyReader propertyReader = new PropertyReader();
	private static boolean processing = false;
	
	public static void main(String[] args) {	
		try {			
			propertyReader.loadFile("cfg/nofront.properties");
			String language = propertyReader.getProperty("language");
			String country = propertyReader.getProperty("country");
			if (language==null||country==null) {
				language="";
				country="";
			}
			
			messageFinder.setLocale(language,country);
			messageFinder.loadFile("messages");
			gameLauncher.addFolderAndExec(propertyReader.getProperty("nesRomsDir"), propertyReader.getProperty("nesExec"));
			gameLauncher.addFolderAndExec(propertyReader.getProperty("snesRomsDir"), propertyReader.getProperty("snesExec"));
			gameLauncher.addFolderAndExec(propertyReader.getProperty("genesisRomsDir"), propertyReader.getProperty("genesisExec"));			
			if (Boolean.parseBoolean(propertyReader.getProperty("silent"))) {
				process();
			} else {
				createWindow();	
			}			
		} catch (IOException ex) {
			logInfo(ex.toString());
			alert(ex.toString());
			System.exit(1);
		}		
	}
	
	private static synchronized void process() {
		processing = true;
		String rom = "";		
		int count=0;
		int retries = Integer.parseInt(propertyReader.getProperty("retries"));
		logInfo(messageFinder.find("starting_cam"));
		if (!qrCodeReader.open()) {
			logInfo(messageFinder.find("no_cam"));
			alert(messageFinder.find("no_cam"));
			processing = false;
			return;
		}
		Boolean problemOrEnded = false;
		while (count<retries) {
			try {			
				//Take picture and try to read it. Every time it fails it goes to the catch block.
				rom = qrCodeReader.read();
				logInfo(messageFinder.find("launching_rom"),rom);
				if (!gameLauncher.launch(rom)) {
					alert(messageFinder.find("rom_not_found").replace("{}", rom));
				}
				problemOrEnded=true;
			} catch (NotFoundException|FormatException|ChecksumException e) {
				if (count==0) {
					logger.info(messageFinder.find("read_fail"),retries);
				}
				count++;
			} catch (Exception e) {
				logger.error(e.toString());
				alert(e.toString());
				problemOrEnded=true;
			}
			if (problemOrEnded) {
				break;
			}
		}
		if (qrCodeReader.isReading()) {
			logInfo(messageFinder.find("closing_cam"));
			qrCodeReader.close();
			logInfo(messageFinder.find("cam_closed"));
		}
		logInfo("----------");
		processing = false;
	}

	private static void createWindow() {
		final String staticImage = "staticImage";
		final String movingImage = "movingImage";
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
						label.setIcon(new ImageIcon("img/"+propertyReader.getProperty(movingImage)));						
						process();
						label.setIcon(new ImageIcon("img/"+propertyReader.getProperty(staticImage)));
					};
					new Thread(scanTask).start(); 
				}
			}             
	    }			
		JDialog mainWindow = new JDialog();
		JLabel labelForGIF = new JLabel(new ImageIcon("img/"+propertyReader.getProperty(staticImage)));
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
		mainWindow.setLocation(((int)screenWidth-mainWindow.getWidth()), ((int)screenHeight-mainWindow.getHeight()-40));
		mainWindow.setIconImage(Toolkit.getDefaultToolkit().getImage("img/icon.png"));
		mainWindow.setVisible(true);
		mainWindow.setAlwaysOnTop(Boolean.parseBoolean(propertyReader.getProperty("alwaysOnTop")));		
		mainWindow.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0), "exit");
		mainWindow.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0), "launch");
		mainWindow.getRootPane().getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_F4,InputEvent.ALT_DOWN_MASK), "exit");		
		mainWindow.getRootPane().getActionMap().put("exit", new AbstractAction() {
			private static final long serialVersionUID = 2893748791670397467L;
			@Override
		     public void actionPerformed(ActionEvent e) {
				if (!processing) {				
				    mainWindow.dispose();
				    System.exit(0);
				} else {
					alert(messageFinder.find("wait_processing"));
				}
		     }
		});
		mainWindow.getRootPane().getActionMap().put("launch", new AbstractAction() {
			private static final long serialVersionUID = 5463115862508920904L;
			@Override
		     public void actionPerformed(ActionEvent e) {
				if (!processing) {
					Runnable scanTask = () -> {						
						labelForGIF.setIcon(new ImageIcon("img/"+propertyReader.getProperty(movingImage)));						
						process();
						labelForGIF.setIcon(new ImageIcon("img/"+propertyReader.getProperty(staticImage)));
					};
					new Thread(scanTask,"Process-Thread").start(); 
				}				
		     }
		});		
	}
	
	private static void alert(String message) {
		JOptionPane.showMessageDialog(null, message);		
	}
	
	private static void logInfo(String message) {
		if(logger.isInfoEnabled()) {
			logger.info(message);
		}		
	}

	private static void logInfo(String message, String param) {
		if(logger.isInfoEnabled()) {
			logger.info(message,param);
		}		
	}	
}
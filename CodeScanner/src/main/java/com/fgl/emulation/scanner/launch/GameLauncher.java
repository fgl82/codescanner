package com.fgl.emulation.scanner.launch;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class GameLauncher {
	private Map <String,String> foldersAndExecs = new HashMap<>();
	
	public void addFolderAndExec(String folder, String exec) {
		foldersAndExecs.put(folder, exec);
	}
			
	public Map<String, String> getFoldersAndExecs() {
		return foldersAndExecs;
	}
	
	public void setFoldersAndExecs(Map<String, String> foldersAndExecs) {
		this.foldersAndExecs = foldersAndExecs;
	}

	private String getLaunchCommand(String rom) {
		String launchCommand = "";
		for (Entry<String, String> entry : foldersAndExecs.entrySet()) {
			String folder = entry.getKey();
			Object exec = entry.getValue();
			if (fileExistsInDir(rom,folder)) {
				launchCommand = exec+" \""+folder+File.separator+rom+"\"";
			}
		}		  
		return launchCommand;
	}

	public boolean launch(String rom) throws IOException {
		String launchCommand = getLaunchCommand(rom);
		boolean result = false;
		if(!launchCommand.isEmpty()) {
			Runtime.getRuntime().exec(launchCommand);
			result = true;
		}
		return result;
	}

	private boolean fileExistsInDir(String file,String dir) {
		return new File(dir,file).exists();
	}	
}
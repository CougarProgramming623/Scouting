package com.jt.scoutserver.utils;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;

public class Server extends JFrame {
	private static final String FOLDER_NAME = "JT Robo App";
	private static final File APPDATA_STORAGE_FOLDER = new File(System.getenv("APPDATA"), FOLDER_NAME);
	private static final File MATCHES_DIR = new File(APPDATA_STORAGE_FOLDER, "Matches");
	private static final String ANDROID_SAVE_FILE = "/sdcard/";
	private JTextArea console = new JTextArea(40, 40);
	private JList<MatchSumbission> list;
	
	private void refresh() {
		String[] files = AndroidUtils.listFiles(FOLDER_NAME);
		if(files.length == 1 && files[0] == AndroidUtils.ADB_ERROR) 
	}
	
}

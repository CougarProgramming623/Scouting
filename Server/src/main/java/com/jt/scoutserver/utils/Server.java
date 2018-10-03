package com.jt.scoutserver.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JTextArea;

import com.jt.scoutcore.MatchSubmission;

import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

public class Server extends JFrame {
	private static final String FOLDER_NAME = "JT Robo App";
	private static final File APPDATA_STORAGE_FOLDER = new File(System.getenv("APPDATA"), FOLDER_NAME);
	private static final File MATCHES_DIR = new File(APPDATA_STORAGE_FOLDER, "Matches");
	private static final String ANDROID_SAVE_FILE = "/sdcard/" + FOLDER_NAME;
	private JTextArea console = new JTextArea(40, 40);
	private JList<MatchSubmission> list;
	
	private void pull() {
		try {
			JadbConnection jadb = new JadbConnection();
			List<JadbDevice> devices = jadb.getDevices();
			for(JadbDevice device : devices) {
				
			}
		
		
		} catch (IOException | JadbException e) {
			throw new RuntimeException(e);
		}
		
	}
	
}

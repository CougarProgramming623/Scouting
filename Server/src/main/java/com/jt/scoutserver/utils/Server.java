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
import se.vidstige.jadb.RemoteFile;

public class Server extends JFrame {
	private static final String FOLDER_NAME = "JT Robo App";
	private static final File APPDATA_STORAGE_FOLDER = new File(System.getenv("APPDATA"), FOLDER_NAME);
	private static final File MATCHES_DIR = new File(APPDATA_STORAGE_FOLDER, "Matches");
	private static final String ANDROID_SAVE_DIRECTORY = "/sdcard/" + FOLDER_NAME;
	private JTextArea console = new JTextArea(40, 40);
	private JList<MatchSubmission> list;

	public Server() {
		super("Scouting App Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void pull() {
		MATCHES_DIR.mkdirs();
		File file = new File("C:\\Users\\Troy Neubauer\\Desktop\\test.xlsx");
		try {
			JadbConnection jadb = new JadbConnection();
			List<JadbDevice> devices = jadb.getDevices();
			for (JadbDevice device : devices) {
				System.out.println("Device: " + device);
				device.execute("mkdirs", ANDROID_SAVE_DIRECTORY);
				List<RemoteFile> files = device.list(ANDROID_SAVE_DIRECTORY);
				if (files.size() == 0)
					System.out.println("No new files to pull...");
				for (RemoteFile remoteFile : files) {
					if (remoteFile.isDirectory())
						continue;// This could be a symptom of another problem...
					System.out.println("pulling file " + remoteFile.getPath());
					File local = new File(MATCHES_DIR, remoteFile.getPath());
					if (local.exists()) {
						System.out.println("skipping already existing file " + remoteFile.getPath());
						continue;
					}
					device.pull(new RemoteFile(ANDROID_SAVE_DIRECTORY + "/" + remoteFile.getPath()), new File(MATCHES_DIR, remoteFile.getPath()));
				}
			}

		} catch (IOException | JadbException e) {
			throw new RuntimeException(e);
		}

	}

}

package com.jt.scoutserver.utils;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.jt.scoutcore.MatchSubmission;
import com.jt.scoutcore.ScoutingUtils;

import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.RemoteFile;

public class Server extends JFrame {
	private static final String FOLDER_NAME = "JT Robo App";
	private static final File APPDATA_STORAGE_FOLDER = new File(System.getenv("APPDATA"), FOLDER_NAME);
	private static final File MATCHES_DIR = new File(APPDATA_STORAGE_FOLDER, "Matches");
	private static final String ANDROID_SAVE_DIRECTORY = "/sdcard/" + FOLDER_NAME;
	private JTextArea console = new JTextArea(10, 30);
	private DefaultListModel<MatchSubmission> model = new DefaultListModel<MatchSubmission>();
	private JList<MatchSubmission> list = new JList<MatchSubmission>(model);

	public Server() {
		super("Scouting App Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel(new BorderLayout());
		console.setEditable(false);
		JScrollPane scroll = new JScrollPane(console);
		scroll.setBorder(BorderFactory.createTitledBorder("Console"));
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(scroll, BorderLayout.SOUTH);

		setContentPane(panel);
		setVisible(true);

		PrintStream origionalOut = System.out;
		System.setOut(new PrintStream(new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				String currentText = console.getText();
				char[] newArray = new char[currentText.length() + 1];
				currentText.getChars(0, currentText.length(), newArray, 0);
				newArray[currentText.length()] = (char) b;
				console.setText(new String(newArray));
				origionalOut.write(b);
			}
		}));

		for (File file : MATCHES_DIR.listFiles()) {
			if (file.isDirectory())
				continue;
			try {
				MatchSubmission submission = ScoutingUtils.read(file);
				if (!model.contains(submission))
					model.addElement(submission);
			} catch (Exception e) {
				// ignore
				System.out.println("invalid file " + file);
			}
		}
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				SystemUtils.nativeExit();
				System.out.println("closing");
			}
		});
	}

	public void pull() {
		MATCHES_DIR.mkdirs();
		System.out.println("\nPreparing to pull fines...");
		try {
			JadbConnection jadb = new JadbConnection();
			List<JadbDevice> devices = jadb.getDevices();
			for (JadbDevice device : devices) {
				device.execute("mkdirs", ANDROID_SAVE_DIRECTORY);
				List<RemoteFile> files = device.list(ANDROID_SAVE_DIRECTORY);
				if (files.size() == 0)
					System.out.println("No new files to pull from " + device.toString());
				for (RemoteFile remoteFile : files) {
					if (remoteFile.isDirectory())
						continue;// This could be a symptom of another problem...
					File local = new File(MATCHES_DIR, remoteFile.getPath());
					if (local.exists()) {
						System.out.println("Skipping already existing file " + remoteFile.getPath() + " on " + device.toString());
						continue;
					}
					device.pull(new RemoteFile(ANDROID_SAVE_DIRECTORY + "/" + remoteFile.getPath()), new File(MATCHES_DIR, remoteFile.getPath()));
					System.out.println("Pulled file " + remoteFile.getPath() + " from " + device.toString());
				}
			}

		} catch (IOException | JadbException e) {
			Utils.showNotification("Unable to access an android device", "Failed to create an adb connection...");
			System.out.println("Failed to connect to adb");
			e.printStackTrace(System.out);
		}

	}

}

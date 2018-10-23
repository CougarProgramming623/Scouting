package com.jt.scoutserver.utils;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import com.jt.scoutserver.Main;

public class Utils {

	public static File saveFile(String extension, String desc) {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Save File");
		chooser.setApproveButtonText("Save");
		chooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return desc;
			}

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || FilenameUtils.isExtension(f.getName(), extension);
			}
		});

		chooser.showOpenDialog(null);
		File file = chooser.getSelectedFile();
		if (file == null)
			return null;
		if (file.exists() && file.isFile()) {
			int value = JOptionPane.showOptionDialog(null, "Do you want to override this file?", file.getName() + " already exists!", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, null,
					null);
			if (value != JOptionPane.YES_OPTION) {
				return null;
			}
		}
		if (FilenameUtils.isExtension(file.getName(), extension))
			return file;
		else
			return new File(file.getPath() + '.' + extension);
	}

	public static void showError(String title, String error) {
		JOptionPane.showMessageDialog(null, error, title, JOptionPane.ERROR_MESSAGE);
	}

	public static void showInfo(String title, String info) {
		JOptionPane.showMessageDialog(null, info, title, JOptionPane.INFORMATION_MESSAGE);
	}

	private static boolean[] usage = new boolean[100];

	private static int findNextPlace() {
		for (int i = 0; i < usage.length; i++) {
			if (!usage[i]) {
				usage[i] = true;
				return i;
			}
		}
		return -1;
	}

	public static void showNotification(String title, String body) {
		String header = title;
		String message = body;
		JFrame frame = new JFrame();
		frame.setSize(300, 125);
		frame.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 1.0f;
		constraints.weighty = 1.0f;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.fill = GridBagConstraints.BOTH;
		JLabel headingLabel = new JLabel(header);
		// headingLabel.setIcon(headingIcon); // --- use image icon you want to be as heading image.
		headingLabel.setOpaque(false);
		frame.add(headingLabel, constraints);
		constraints.gridx++;
		constraints.weightx = 0f;
		constraints.weighty = 0f;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.NORTH;
		JButton cloesButton = new JButton("X");
		cloesButton.setMargin(new Insets(1, 4, 1, 4));
		cloesButton.setFocusable(false);
		frame.add(cloesButton, constraints);
		int place = findNextPlace();
		cloesButton.addActionListener((e) -> {
			frame.dispose();
			usage[place] = false;
		});
		constraints.gridx = 0;
		constraints.gridy++;
		constraints.weightx = 1.0f;
		constraints.weighty = 1.0f;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.fill = GridBagConstraints.BOTH;
		JLabel messageLabel = new JLabel("<HtMl>" + message);
		frame.add(messageLabel, constraints);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.setFocusable(false);

		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();// size of the screen
		Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());// height of the task bar
		frame.pack();
		frame.setLocation(scrSize.width - frame.getWidth(), scrSize.height - toolHeight.bottom - (frame.getHeight() + 10) * (place + 1));
		frame.setVisible(true);
		Thread thread = new Thread(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				throw new RuntimeException(e1);
			}
			frame.dispose();
			usage[place] = false;
		});
		thread.start();
	}
}

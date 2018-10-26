package com.jt.scoutserver.utils;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

import com.jt.scoutcore.MatchSubmission;
import com.jt.scoutcore.ScoutingConstants;
import com.jt.scoutcore.ScoutingUtils;
import com.jt.scoutserver.Server;

public class Utils {

	public static String toProperEnglishName(String javaName) {
		
		if (javaName.toUpperCase().equals(javaName)) {// All letters are uppercase. Its the name of a constant
			char[] result = new char[javaName.length()];
			boolean lastWasSpace = true;// True to capitalize the first one
			for (int i = 0; i < result.length; i++) {
				char current = javaName.charAt(i);
				if (Character.isLetter(current)) {
					result[i] = lastWasSpace ? current : Character.toLowerCase(current);
					lastWasSpace = false;
				} else if (current == '_') {
					result[i] = ' ';
					lastWasSpace = true;
				} else {// Must be something else. Maybe a digit?
					result[i] = current;// So just assign it
					lastWasSpace = false;
				}
			}
			return new String(result);
		} else {// probably camel case
			if (javaName.indexOf(' ') != -1) {// bad... Can't be camel case because of spaces
				return javaName;// Wrong
			}
			StringBuilder sb = new StringBuilder(javaName.length());
			boolean first = true;// True to capitalize the first one
			for (int i = 0; i < javaName.length(); i++) {
				char current = javaName.charAt(i);
				if (Character.isLetter(current)) {
					if (Character.isUpperCase(current) || first) {// || first to make sure the first letter is capitalized
						if (!first)
							sb.append(' ');
						sb.append(Character.toUpperCase(current));
					} else {
						sb.append(current);
					}
				} else {// Must be something else. Maybe a digit?
					sb.append(current);// So just assign it
				}
				first = false;
			}

			return sb.toString();
		}
	}

	public static abstract class Operator {
		private String name = "";

		public abstract boolean evaluate(String left, String right);

		@Override
		public String toString() {
			return name;
		}
	}

	public static final Operator EQUAL_TO = new Operator() {
		@Override
		public boolean evaluate(String left, String right) {
			try {
				try {
					double a = Double.parseDouble(left), b = Double.parseDouble(right);
					return a == b;
				} catch (Exception e) {
				} // We couldn't compare using doubles
				int aInt = Integer.parseInt(left), bInt = Integer.parseInt(right);
				return aInt == bInt;
			} catch (Exception e) {// Parsing using both int and double didn't work, so default to string
				left = left.replaceAll(" ", "");
				left = left.replaceAll("_", "");
				return left.equalsIgnoreCase(right);
			}
		}

	};

	public static final Operator CONTAINS = new Operator() {
		@Override
		public boolean evaluate(String left, String right) {
			return left.toLowerCase().contains(right.toLowerCase());
		}

	};

	public static final Operator LESS_THAN = new Operator() {
		@Override
		public boolean evaluate(String left, String right) {
			try {
				try {
					double a = Double.parseDouble(left), b = Double.parseDouble(right);
					return a < b;
				} catch (Exception e) {
				} // We couldn't compare using doubles
				int aInt = Integer.parseInt(left), bInt = Integer.parseInt(right);
				return aInt < bInt;
			} catch (Exception e) {// Parsing using both int and double didn't work, so default to string
				throw new RuntimeException("Cant compare non numerical types");
			}
		}

	};

	public static final Operator GREATER_THAN = new Operator() {
		@Override
		public boolean evaluate(String left, String right) {
			try {
				try {
					double a = Double.parseDouble(left), b = Double.parseDouble(right);
					return a > b;
				} catch (Exception e) {
				} // We couldn't compare using doubles
				int aInt = Integer.parseInt(left), bInt = Integer.parseInt(right);
				return aInt > bInt;
			} catch (Exception e) {// Parsing using both int and double didn't work, so default to string
				throw new RuntimeException("Cant compare non numerical types");
			}
		}

	};

	/**
	 * Returns a list of files that have the given attribute
	 * 
	 * @param attribute The attribute in each file to check for
	 * @param operator The operator to use
	 * @param other The value to check against
	 * @return The list of files that are true on those attributes
	 */
	public static List<File> getFilesWith(String attribute, Operator operator, String other) {
		List<File> result = new ArrayList<File>();
		for (File childFile : Server.MATCHES_DIR.listFiles()) {
			if (childFile.exists() && childFile.isFile() && FilenameUtils.isExtension(childFile.getName(), ScoutingConstants.EXTENSION)) {
				MatchSubmission sub = ScoutingUtils.read(childFile);
				if (sub.has(attribute)) {
					String value = sub.get(attribute).toString();
					if (operator.evaluate(value, other)) {
						result.add(childFile);
					}
				}
			}
		}
		return result;
	}

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

	public static String randomString(Random random, int length) {
		char[] chars = new char[length];
		for (int i = 0; i < length; i++) {
			chars[i] = (char) (random.nextInt(127 - 33) + 33);
		}
		return new String(chars);
	}

	public static int getIntInput(int min, int max, String title, String message) {
		do {
			String input = JOptionPane.showInputDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
			try {
				if (input == null)
					return Integer.MIN_VALUE;
				int value = Integer.parseInt(input);
				if (value <= max && value >= min) {
					return value;
				}
			} catch (Exception e) {
			}
		} while (true);
	}

	static {
		for (Field field : Utils.class.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers()) && Utils.Operator.class.isAssignableFrom(field.getType())) {
				try {
					Operator operator = (Operator) field.get(null);
					operator.name = toProperEnglishName(field.getName());
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
			}
		}
	}
}

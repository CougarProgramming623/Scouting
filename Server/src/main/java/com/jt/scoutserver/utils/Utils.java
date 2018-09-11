package com.jt.scoutserver.utils;

import javax.swing.JOptionPane;

public class Utils {
	public static void showError(String title, String error) {
		JOptionPane.showMessageDialog(null, error, title, JOptionPane.ERROR_MESSAGE);
	}

	public static void showInfo(String title, String info) {
		JOptionPane.showMessageDialog(null, info, title, JOptionPane.INFORMATION_MESSAGE);
	}
}

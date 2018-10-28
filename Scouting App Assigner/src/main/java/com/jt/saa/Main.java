package com.jt.saa;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Main {

	public static void main(String[] args) {
		System.out.println("Starting Scouting App Pre-Match Assigner!");
		JFileChooser chooser = new JFileChooser();

		File excelFile = chooser.getSelectedFile();
		int numDevices = Integer.parseInt(JOptionPane.showInputDialog(chooser, "Enter the number of devices to export for", "Enter Number of Devices", JOptionPane.INFORMATION_MESSAGE));
		
		int matchC = -1;
		List<Integer> redTeams = new ArrayList<Integer>(), blueTeams = new ArrayList<Integer>();
		
	}
}

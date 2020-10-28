package com.jt.saa;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.jt.scoutcore.AssignedTeams;
import com.jt.scoutcore.AssignerEntry;
import com.jt.scoutcore.AssignerList;
import com.jt.scoutcore.ScoutingConstants;
import com.jt.scoutcore.ScoutingUtils;
import com.jt.scoutserver.utils.ExcelUtils;
import com.jt.scoutserver.utils.Utils;

import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.RemoteFile;

public class Main {

	public static final String PHONE_SAVE_DIR = ScoutingUtils.getSaveDir(), PHONE_ASSIGNMENT_FILE = PHONE_SAVE_DIR + ScoutingConstants.ANDROID_ASSIGNMENTS_FILE_NAME;
	private static int matchStart = 0;

	private static boolean[] deviceIsRed;

	private static long lastTime = System.currentTimeMillis();

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		System.out.println("Starting Scouting App Pre-Match Assigner!");
		File excelFile = Utils.openFile(ExcelUtils.EXCEL_EXTENSION, "Excel Files");
		if (excelFile == null)
			return;
		int numDevices = Utils.getIntInput(1, 100, "Enter Number of Devices", "Enter the number of devices to export for");
		if (numDevices == Integer.MIN_VALUE)
			return;
		int result = JOptionPane.showConfirmDialog(null, "Do you want to pre-set the matches (yes) using the file or pre-set the teams (no)", "Set Mode", JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			System.out.println("Presetting matches");
			matchStart = Utils.getIntInput(0, 10000, "Enter Match Number to start on", "Enter 0 to start on the first match");
			if (matchStart == Integer.MIN_VALUE)
				return;
			int[] deviceUsages = new int[numDevices];// maps device id's to number of matches assigned
			deviceIsRed = new boolean[numDevices];
			for (int i = 0; i < deviceIsRed.length / 2; i++) {
				deviceIsRed[i] = true;// Make the first half red
			}
			ArrayList<AssignerEntry>[] deviceOutputs = new ArrayList[numDevices];
			for (int i = 0; i < deviceOutputs.length; i++) {
				deviceOutputs[i] = new ArrayList<AssignerEntry>();
			}

			// matchColumn = excel column at which the match # is stored
			//
			int matchColumn = -1;
			List<Integer> redColumns = new ArrayList<Integer>(), blueColumns = new ArrayList<Integer>();

			Workbook workbook = ExcelUtils.openExcelFile(excelFile);
			Sheet sheet = workbook.getSheetAt(0);
			Row header = sheet.getRow(0);
			Iterator<Cell> iter = header.cellIterator();
			while (iter.hasNext()) {
				Cell cell = iter.next();
				if (cell.getCellTypeEnum() == CellType.STRING) {
					String value = cell.getStringCellValue();
					if (value.equalsIgnoreCase("match")) {
						if (matchColumn != -1)
							System.err.println("Second match column detected! Invalid file!");
						matchColumn = cell.getColumnIndex();
					} else if (value.equalsIgnoreCase("red")) {
						redColumns.add(cell.getColumnIndex());
					} else if (value.equalsIgnoreCase("blue")) {
						blueColumns.add(cell.getColumnIndex());
					}
				}
			}
			if (matchColumn == -1) {
				System.err.println("Missing match column!!\nExiting!");
				return;
			}
			if (redColumns.size() == 0) {
				System.err.println("No red columns!\nExiting!");
				return;
			}
			if (blueColumns.size() == 0) {
				System.err.println("No blue columns!\nExiting!");
				return;
			}
			Iterator<Row> rowIter = sheet.iterator();
			rowIter.next();// Skip the header row
			while (rowIter.hasNext()) {
				Row row = rowIter.next();
				Cell matchCell = row.getCell(matchColumn);
				List<Cell> reds = new ArrayList<Cell>();
				List<Cell> blues = new ArrayList<Cell>();
				boolean good = true;
				for (Integer redCol : redColumns) {
					Cell cell = row.getCell(redCol);
					if (cell == null || cell.getCellTypeEnum() != CellType.NUMERIC) {
						good = false;
						continue;
					}
					reds.add(cell);
				}
				for (Integer blueCol : blueColumns) {
					Cell cell = row.getCell(blueCol);
					if (cell == null || cell.getCellTypeEnum() != CellType.NUMERIC) {
						good = false;
						continue;
					}
					blues.add(cell);
				}
				if (matchCell == null)
					good = false;
				if (!good) {
					System.err.println("Invalid line at row=" + (row.getRowNum() + 1));
					continue;
				}

				int match = (int) matchCell.getNumericCellValue();
				List<Integer> blueTeams = new ArrayList<Integer>(), redTeams = new ArrayList<Integer>();

				for (Cell cell : reds) {
					redTeams.add((int) cell.getNumericCellValue());
				}

				for (Cell cell : blues) {
					blueTeams.add((int) cell.getNumericCellValue());
				}
				int totalTeams = redTeams.size() + blueTeams.size();
				if (totalTeams != redColumns.size() + blueColumns.size()) {
					System.out.println("missing team at row=" + (row.getRowNum() + 1));
				}


				AssignerEntry entry;
				while ((entry = getEntry(redTeams, blueTeams, match)) != null) {
					int device = getLeastDevice(deviceUsages, entry);
					if (device == -1) {
						throw new RuntimeException("Unable to find a device to assign " + entry + " to");
					}
					deviceUsages[device]++;
					deviceOutputs[device].add(entry);
				}

			}

			List<JadbDevice> writtenDevices = new ArrayList<JadbDevice>();
			for (int i = 0; i < deviceOutputs.length;) {
				try {
					File file = new File("./temp" + Integer.toString(i) + ".dat");
					AssignerList list = new AssignerList(deviceOutputs[i], matchStart);
					ScoutingUtils.write(file, list, AssignerList.class);

					try {
						JadbConnection connection = new JadbConnection();
						List<JadbDevice> devices = connection.getDevices();
						JadbDevice currentDevice = null;
						for (JadbDevice device : devices) {
							if (!writtenDevices.contains(device)) {// We found one we havn't pushed to
								currentDevice = device;
							} else {
							}
						}
						if (currentDevice == null) {
							if (System.currentTimeMillis() - lastTime > 1000) {
								lastTime = System.currentTimeMillis();
								System.out.println("Waiting for new devices...");
							}
							try {
								Thread.sleep(1);
							} catch (Exception e) {
							}
							continue;
						}
						System.out.println("Attempting to write to device " + (i + 1) + " : " + deviceOutputs[i].toString());
						RemoteFile remoteFile = new RemoteFile(PHONE_ASSIGNMENT_FILE);
						currentDevice.push(file, remoteFile);
						writtenDevices.add(currentDevice);
						currentDevice.execute("rm", "-r", "sdcard/JT\\ Robo\\ App/matches/");
						i++;
						Thread.sleep(20);
						file.delete();
					} catch (Exception e) {
						Utils.showError("Failed to save file on device!", "Failed to write to device " + e.getClass() + " : " + e.getMessage());
						throw new RuntimeException(e);
					}
				} catch (Exception e) {
					// throw new RuntimeException(e);
				}
			}
		} else {
			System.out.println("Presetting teams");

			ArrayList<Integer> teamsList = new ArrayList<Integer>();

			Workbook workbook = ExcelUtils.openExcelFile(excelFile);
			Sheet sheet = workbook.getSheetAt(0);
			Row header = sheet.getRow(0);
			Iterator<Cell> iter = header.cellIterator();
			int col = -1;
			while (iter.hasNext()) {
				Cell cell = iter.next();
				if (cell.getCellTypeEnum() == CellType.STRING) {
					String value = cell.getStringCellValue();
					if (value.equalsIgnoreCase("team")) {
						if (col != -1)
							System.err.println("Second match column detected! Invalid file!");
						col = cell.getColumnIndex();
					} else if (value.equalsIgnoreCase("teams")) {
						if (col != -1)
							System.err.println("Second match column detected! Invalid file!");
						col = cell.getColumnIndex();
					}
				}
			}
			if (col == -1) {
				System.err.println("Unable to find teams column");
				System.exit(1);
			}

			Iterator<Row> rowIter = sheet.rowIterator();
			while (rowIter.hasNext()) {
				Row row = rowIter.next();
				Cell cell = row.getCell(col);
				if (cell != null) {
					if (cell.getCellTypeEnum().equals(CellType.BLANK)) {
						System.err.println("Invalid column at col =" + (col + 1) + " row = " + (row.getRowNum() + 1));
						continue;
					} else if (cell.getCellTypeEnum().equals(CellType.BOOLEAN)) {
						System.err.println("Invalid column at col =" + (col + 1) + " row = " + (row.getRowNum() + 1));
						continue;
					} else if (cell.getCellTypeEnum().equals(CellType.ERROR)) {
						System.err.println("Invalid column at col =" + (col + 1) + " row = " + (row.getRowNum() + 1));
						continue;
					} else if (cell.getCellTypeEnum().equals(CellType.FORMULA)) {
						System.err.println("Invalid column at col =" + (col + 1) + " row = " + (row.getRowNum() + 1));
						continue;
					} else if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
						double value = cell.getNumericCellValue();
						int team = (int) value;
						teamsList.add(team);
					} else if (cell.getCellTypeEnum().equals(CellType.STRING)) {
						String value = cell.getStringCellValue();
						try {
							int team = Integer.parseInt(value);
							teamsList.add(team);
						} catch (Exception e) {
							System.err.println("Invalid column at col =" + (col + 1) + " row = " + (row.getRowNum() + 1));
							continue;
						}
					}
				}
			}

			int[] teamsArray = new int[teamsList.size()];
			for (int i = 0; i < teamsArray.length; i++) {
				teamsArray[i] = teamsList.get(i);
			}

			AssignedTeams teams = new AssignedTeams(teamsArray);

			File file = new File("./temp.dat");
			ScoutingUtils.write(file, teams, AssignedTeams.class);

			List<JadbDevice> writtenDevices = new ArrayList<JadbDevice>();
			for (int i = 0; i < numDevices;) {
				try {

					try {
						JadbConnection connection = new JadbConnection();
						List<JadbDevice> devices = connection.getDevices();
						JadbDevice currentDevice = null;
						for (JadbDevice device : devices) {
							if (!writtenDevices.contains(device)) {// We found one we havn't pushed to
								currentDevice = device;
							} else {
							}
						}
						if (currentDevice == null) {
							if (System.currentTimeMillis() - lastTime > 1000) {
								lastTime = System.currentTimeMillis();
								System.out.println("Waiting for new devices...");
							}
							try {
								Thread.sleep(1);
							} catch (Exception e) {
							}
							continue;
						}
						System.out.println("Attempting to write to device " + (i + 1) + " : " + teams.toString());
						RemoteFile remoteFile = new RemoteFile(PHONE_ASSIGNMENT_FILE);
						currentDevice.push(file, remoteFile);
						writtenDevices.add(currentDevice);
						currentDevice.execute("rm", "-r", "sdcard/JT\\ Robo\\ App/matches/");
						i++;
						Thread.sleep(20);
					} catch (Exception e) {
						Utils.showError("Failed to save file on device!", "Failed to write to device " + e.getClass() + " : " + e.getMessage());
						throw new RuntimeException(e);
					}
				} catch (Exception e) {
					// throw new RuntimeException(e);
				}
			}
			file.delete();
		}

	}

	// Returns the key of the device in the map which has the least value
	private static int getLeastDevice(int[] deviceUsages, AssignerEntry entry) {
		int deviceIndex = -1;
		for (int i = 0; i < deviceUsages.length; i++) {
//			if (entry.red == deviceIsRed[i]) { // Only allow adding a red entry to a red device or vice versa
				if (deviceIndex == -1 || (deviceUsages[i] < deviceUsages[deviceIndex])) {
					deviceIndex = i;
				}
//			}
		}
		return deviceIndex;
	}

	private static AssignerEntry getEntry(List<Integer> redTeams, List<Integer> blueTeams, int match) {
		if (redTeams.size() > 0) {
			return new AssignerEntry(match, redTeams.remove(0), true);
		} else if (blueTeams.size() > 0) {
			return new AssignerEntry(match, blueTeams.remove(0), false);
		} else {
			return null;
		}
	}

}

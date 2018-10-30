package com.jt.saa;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.jt.scoutcore.AssignerEntry;
import com.jt.scoutcore.ScoutingConstants;
import com.jt.scoutserver.utils.ExcelUtils;
import com.jt.scoutserver.utils.Utils;

public class Main {

	public static void main(String[] args) {
		System.out.println("Starting Scouting App Pre-Match Assigner!");
		File excelFile = Utils.openFile(ExcelUtils.EXCEL_EXTENSION, "Excel Files");
		int numDevices = Utils.getIntInput(1, 100, "Enter Number of Devices", "Enter the number of devices to export for");
		int[] deviceUsages = new int[numDevices];// maps device id's to number of matches assigned
		ArrayList<AssignerEntry>[] deviceOutputs = new ArrayList[numDevices];
		for (int i = 0; i < deviceOutputs.length; i++) {
			deviceOutputs[i] = new ArrayList<AssignerEntry>();
		}

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
		System.out.println("red " + redColumns);
		System.out.println("blue " + blueColumns);
		System.out.println("match " + matchColumn);
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
				int device = getLeastDevice(deviceUsages);
				deviceUsages[device]++;
				deviceOutputs[device].add(entry);
			}

		}
		for (int i = 0; i < deviceOutputs.length; i++) {
			try {
				File file = new File(Integer.toString(i));
				Output out = new Output(new FileOutputStream(file));
				Kryo kryo = ScoutingConstants.KRYO.get();
				kryo.writeObject(out, deviceOutputs[i]);
				out.close();
				System.out.println("device " + i + " " + deviceOutputs[i].toString());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	// Returns the key of the device in the map which has the least value
	private static int getLeastDevice(int[] deviceUsages) {
		int deviceIndex = 0;
		for (int i = 1; i < deviceUsages.length; i++) {
			if (deviceUsages[i] < deviceUsages[deviceIndex]) {
				deviceIndex = i;
			}
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

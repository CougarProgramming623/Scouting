package com.jt.saa;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.jt.scoutserver.utils.ExcelUtils;
import com.jt.scoutserver.utils.Utils;

public class Main {

	public static void main(String[] args) {
		System.out.println("Starting Scouting App Pre-Match Assigner!");
		File excelFile = Utils.openFile(ExcelUtils.EXCEL_EXTENSION, "Excel Files");
		int numDevices = Utils.getIntInput(1, 100, "Enter Number of Devices", "Enter the number of devices to export for");
		HashMap<Integer, Integer> deviceUsages = new HashMap<Integer, Integer>();// maps device id's to number of matches assigned
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
				System.err.println("Invalid line at row=" + row.getRowNum());
				continue;
			}

			int match = (int) matchCell.getNumericCellValue();
			List<Integer> blueTeams = new ArrayList<Integer>(), redTeams = new ArrayList<Integer>();

			for (Cell cell : reds) {
				redTeams.add((int) cell.getNumericCellValue());
			}
			
			for (Cell cell : blues) {
				redTeams.add((int) cell.getNumericCellValue());
			}

		}
	}
}

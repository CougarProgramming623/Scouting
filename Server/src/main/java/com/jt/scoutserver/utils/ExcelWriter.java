package com.jt.scoutserver.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jt.scoutcore.MatchSubmission;
import com.jt.scoutcore.ScoutingConstants;
import com.jt.scoutcore.ScoutingUtils;
import com.jt.scoutserver.Server;

public class ExcelWriter {

	private static HashMap<String, Integer> getHeader(int colStart) {
		// maps column names to column numbers
		HashMap<String, Integer> headers = new HashMap<String, Integer>();
		for (File childFile : Server.MATCHES_DIR.listFiles()) {
			if (childFile.exists() && childFile.isFile() && FilenameUtils.isExtension(childFile.getName(), ScoutingConstants.EXTENSION)) {
				MatchSubmission sub = ScoutingUtils.read(childFile);
				for (Entry<String, Object> entry : sub.getMap().entrySet()) {
					String key = entry.getKey();
					if (!headers.containsKey(key)) {
						headers.put(key, headers.size() + colStart);
					}
				}
			}
		}
		return headers;
	}

	private static void writeRow(Row row, HashMap<String, Integer> headers, MatchSubmission sub) {
		for (Entry<String, Object> entry : sub.getMap().entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			int col = headers.get(key);
			Cell cell = row.createCell(col);

			if (value instanceof Number) {
				cell.setCellValue(((Number) value).doubleValue());
			} else if (value instanceof Boolean) {
				cell.setCellValue(((Boolean) value).booleanValue());
			} else {
				cell.setCellValue(value.toString());
			}

		}

	}

	private static void writeHeader(Row header, HashMap<String, Integer> headers) {
		for (Entry<String, Integer> entry : headers.entrySet()) {
			String key = entry.getKey();
			header.createCell(entry.getValue()).setCellValue(key);
		}
	}

	public static void writeTeam(File file, int team, int rowStart, int colStart) {
		HashMap<String, Integer> headers = new HashMap<String, Integer>();
		for (File childFile : Server.MATCHES_DIR.listFiles()) {
			if (childFile.exists() && childFile.isFile() && FilenameUtils.isExtension(childFile.getName(), ScoutingConstants.EXTENSION)) {
				MatchSubmission sub = ScoutingUtils.read(childFile);
				if (sub.getTeamNumber() == team) {
					for (Entry<String, Object> entry : sub.getMap().entrySet()) {
						String key = entry.getKey();
						if (!headers.containsKey(key)) {
							headers.put(key, headers.size() + colStart);
						}
					}
				}
			}
		}
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		int rowNum = rowStart;
		Row header = sheet.createRow(rowNum++);
		writeHeader(header, headers);

		for (File childFile : Server.MATCHES_DIR.listFiles()) {
			if (childFile.exists() && childFile.isFile() && FilenameUtils.isExtension(childFile.getName(), ScoutingConstants.EXTENSION)) {
				MatchSubmission sub = ScoutingUtils.read(childFile);
				if (sub.getTeamNumber() == team) {
					Row row = sheet.createRow(rowNum++);
					writeRow(row, headers, sub);
				}
			}
		}
		ExcelUtils.writeExcelFile(workbook, file);
		System.out.println("Saved excel file successfully");
	}

	public static void writeAll(File file, int rowStart, int colStart) {
		HashMap<String, Integer> headers = getHeader(colStart);
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		int rowNum = rowStart;
		Row header = sheet.createRow(rowNum++);
		writeHeader(header, headers);

		for (File childFile : Server.MATCHES_DIR.listFiles()) {
			if (childFile.exists() && childFile.isFile() && FilenameUtils.isExtension(childFile.getName(), ScoutingConstants.EXTENSION)) {
				MatchSubmission sub = ScoutingUtils.read(childFile);
				Row row = sheet.createRow(rowNum++);
				writeRow(row, headers, sub);
			}
		}
		ExcelUtils.writeExcelFile(workbook, file);
	}

}

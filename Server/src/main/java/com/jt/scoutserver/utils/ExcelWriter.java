package com.jt.scoutserver.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jt.scoutcore.MatchSubmission;
import com.jt.scoutcore.ScoutingConstants;
import com.jt.scoutcore.ScoutingUtils;
import com.jt.scoutserver.Server;
import com.jt.scoutserver.utils.ExcelUtils.MatchSubmissionIdentifier;

public class ExcelWriter {

	public static HashMap<String, Integer> getHeader(int colStart, MatchSubmissionIdentifier id, long data, List<File> files) {
		// maps column names to column numbers
		HashMap<String, Integer> headers = new HashMap<String, Integer>();
		for (File childFile : files) {
			if (childFile.exists() && childFile.isFile() && FilenameUtils.isExtension(childFile.getName(), ScoutingConstants.EXTENSION)) {
				MatchSubmission sub = ScoutingUtils.read(childFile);
				if (id.matchFile(sub, data)) {
					for (Entry<String, Object> entry : sub.getMap().entrySet()) {
						String key = entry.getKey();
						if (!headers.containsKey(key)) {
							headers.put(key, headers.size() + colStart);
						}
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
			if (value == null)
				cell.setCellValue("No Data");
			else if (value instanceof Number)
				cell.setCellValue(((Number) value).doubleValue());
			else if (value instanceof Boolean)
				cell.setCellValue(((Boolean) value).booleanValue());
			else
				cell.setCellValue(value.toString());

		}

	}

	private static void writeHeader(Sheet sheet, Row header, HashMap<String, Integer> headers) {
		for (Entry<String, Integer> entry : headers.entrySet()) {
			String key = entry.getKey();
			Cell cell = header.createCell(entry.getValue());
			cell.setCellValue(key);
			sheet.setColumnWidth(cell.getColumnIndex(), key.length() * 330);
		}
	}

	public static void write(File file, int rowStart, int colStart, MatchSubmissionIdentifier id, long data) {

		write(file, rowStart, colStart, id, data, allFiles());
	}

	public static List<File> allFiles() {
		List<File> files = new ArrayList<File>();
		for (File f : Server.COMPUTER_MATCHES_DIR.listFiles()) {
			files.add(f);
		}
		return files;
	}

	public static void write(File file, int rowStart, int colStart, MatchSubmissionIdentifier id, long data, List<File> files) {
		HashMap<String, Integer> headers = getHeader(colStart, id, data, files);
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		int rowNum = rowStart;
		Row header = sheet.createRow(rowNum++);
		writeHeader(sheet, header, headers);
		int subCount = 0;

		for (File childFile : files) {
			if (childFile.exists() && childFile.isFile() && FilenameUtils.isExtension(childFile.getName(), ScoutingConstants.EXTENSION)) {
				try {
					MatchSubmission sub = ScoutingUtils.read(childFile);
					if (id.matchFile(sub, data)) {
						Row row = sheet.createRow(rowNum++);
						writeRow(row, headers, sub);
						subCount++;
					}
				} catch (RuntimeException e) {
					System.out.println("Error reading file " + childFile + "\n" + e.getCause().getMessage());
				}
			}
		}
		if (ExcelUtils.writeExcelFile(workbook, file)) {
			Utils.showInfo("Exported file successfully", "Saved data to file \"" + file + "\" successfully\n" + "Includes " + subCount + " match-submissions");
		}
	}

}

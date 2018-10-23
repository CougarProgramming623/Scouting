package com.jt.scoutserver.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jt.scoutcore.MatchSubmission;
import com.jt.scoutcore.ScoutingConstants;
import com.jt.scoutcore.ScoutingUtils;
import com.jt.scoutserver.Server;

public class ExcelUtils {

	public static final String EXCEL_EXTENSION = "xlsx";

	public static Workbook openExcelFile(File file) {
		if (file.exists()) {
			try {
				FileInputStream in = new FileInputStream(file);
				Workbook workbook = WorkbookFactory.create(in);
				in.close();

				return workbook;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new RuntimeException("File not found: " + file);
		}
	}

	public static boolean writeExcelFile(Workbook workbook, File file) {
		try {
			FileOutputStream stream = new FileOutputStream(file);
			try {
				workbook.write(stream);
				stream.close();
				workbook.close();
				return true;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} catch (FileNotFoundException e) {
			if (SystemUtils.isExcelRunning()) {
				Utils.showInfo("Warning!", "Failed to open the file because Excel is running. Please close excel and press ok!");
				if (writeExcelFile(workbook, file))
					Utils.showInfo("Saved File", "Saved file successfully");
				return false;
			} else {
				Utils.showError("Error", "Failed to open file: \"" + file + "\"");
				return false;
			}
		}

	}

	public static void exportTo(File file) {
		// maps column names to column numbers
		HashMap<String, Integer> headers = new HashMap<String, Integer>();
		for (File childFile : Server.MATCHES_DIR.listFiles()) {
			if (childFile.exists() && childFile.isFile() && FilenameUtils.isExtension(childFile.getName(), ScoutingConstants.EXTENSION)) {
				MatchSubmission sub = ScoutingUtils.read(childFile);
				for (Entry<String, Object> entry : sub.getMap().entrySet()) {
					String key = entry.getKey();
					if (!headers.containsKey(key)) {
						headers.put(key, headers.size());
					}
				}
			}
		}
		XSSFWorkbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		int rowNum = 0;
		Row header = sheet.createRow(rowNum++);
		for (Entry<String, Integer> entry : headers.entrySet()) {
			String key = entry.getKey();
			header.createCell(entry.getValue()).setCellValue(key);

		}

		for (File childFile : Server.MATCHES_DIR.listFiles()) {
			if (childFile.exists() && childFile.isFile() && FilenameUtils.isExtension(childFile.getName(), ScoutingConstants.EXTENSION)) {
				MatchSubmission sub = ScoutingUtils.read(childFile);
				Row row = sheet.createRow(rowNum++);
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
		}
		writeExcelFile(workbook, file);
		System.out.println("Saved excel file successfully");
	}

	public static File saveExcelFile() {
		return Utils.saveFile(EXCEL_EXTENSION, "Excel Files");
	}
}

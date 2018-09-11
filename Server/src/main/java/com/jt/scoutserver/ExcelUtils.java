package com.jt.scoutserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelUtils {
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
				Utils.showInfo("Warning!",
						"Failed to open the file because Excel is running. Please close excel and press ok!");
				if (writeExcelFile(workbook, file))
					Utils.showInfo("Saved File", "Saved file successfully");
				return false;
			} else {
				Utils.showError("Error", "Failed to open file: \"" + file + "\"");
				return false;
			}
		}

	}
}

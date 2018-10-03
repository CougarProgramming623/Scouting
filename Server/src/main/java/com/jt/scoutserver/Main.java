package com.jt.scoutserver;

import java.io.File;
import java.time.Instant;
import java.util.Date;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.jt.scoutserver.utils.ExcelUtils;

public class Main {

	public static void main(String[] args) throws Exception {
		File file = new File("C:\\Users\\Troy Neubauer\\Desktop\\test.xlsx");
		while (true) {
			if (file.exists()) {
				Workbook workbook = ExcelUtils.openExcelFile(file);
				Sheet sheet = workbook.getSheetAt(0);
				int rowInt = 0;
				while (sheet.getRow(rowInt) != null) {
					rowInt++;
				}
				sheet.createRow(rowInt).createCell(0).setCellValue(Date.from(Instant.now()));

				ExcelUtils.writeExcelFile(workbook, file);
				System.out.println("done");
			} else {
				XSSFWorkbook workbook = new XSSFWorkbook();
				Sheet sheet = workbook.createSheet();
				Row header = sheet.createRow(0);
				header.createCell(0).setCellValue("Time");

				ExcelUtils.writeExcelFile(workbook, file);
			}
			Thread.sleep(1000);
		}
	}

}

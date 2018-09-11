package com.jt.scoutserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Instant;
import java.util.Date;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main {

	public static void main(String[] args) throws Exception {
		File file = new File("C:\\Users\\Troy Neubauer\\Desktop\\test.xlsx");
		while (true) {
			if (file.exists()) {
				FileInputStream in = new FileInputStream(file);
				Workbook workbook = WorkbookFactory.create(in);
				in.close();
				Sheet sheet = workbook.getSheetAt(0);
				int rowInt = 0;
				while (sheet.getRow(rowInt) != null) {
					rowInt++;
				}
				sheet.createRow(rowInt).createCell(0).setCellValue(Date.from(Instant.now()));

				FileOutputStream stream = new FileOutputStream(file);//Exception thrown here when excel is using it
				//Java.io.FileNotFoundException: *.xlsx (The process cannot access the file because it is being used by another process)
				workbook.write(stream);
				stream.close();
				
				workbook.close();
				System.out.println("done");
			} else {
				XSSFWorkbook workbook = new XSSFWorkbook();
				Sheet sheet = workbook.createSheet();
				Row header = sheet.createRow(0);
				header.createCell(0).setCellValue("Time");

				FileOutputStream stream = new FileOutputStream(file);
				workbook.write(stream);

				stream.close();
				workbook.close();
			}
			Thread.sleep(1000);
		}
	}

}

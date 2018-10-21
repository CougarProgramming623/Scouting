package com.jt.scoutserver;

import com.jt.scoutserver.utils.Server;
import com.jt.scoutserver.utils.SystemUtils;

public class Main {

	public static void main(String[] args) throws Exception {
		Server server = new Server();
		while(true) {
			if(SystemUtils.hasNewDevices()) {
				System.out.println("Detected new device!");
				server.pull();
			}
		}
		
		/*
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
		}*/
	}

}

package com.jt.scoutserver;

import javax.swing.UIManager;

import com.jt.scoutserver.utils.SystemUtils;

public class Main {

	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
 
		Server server = new Server();
		while (true) {
			try {
				if (SystemUtils.hasNewDevices()) {
					System.out.println("Detected new device! Press \"pull\" to pull new files");
					Thread.sleep(1000);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		/*
		 * while (true) { if (file.exists()) { Workbook workbook = ExcelUtils.openExcelFile(file); Sheet sheet = workbook.getSheetAt(0); int rowInt = 0;
		 * while (sheet.getRow(rowInt) != null) { rowInt++; } sheet.createRow(rowInt).createCell(0).setCellValue(Date.from(Instant.now()));
		 * 
		 * ExcelUtils.writeExcelFile(workbook, file); System.out.println("done"); } else { XSSFWorkbook workbook = new XSSFWorkbook(); Sheet sheet =
		 * workbook.createSheet(); Row header = sheet.createRow(0); header.createCell(0).setCellValue("Time");
		 * 
		 * ExcelUtils.writeExcelFile(workbook, file); } Thread.sleep(1000); }
		 */
	}

}

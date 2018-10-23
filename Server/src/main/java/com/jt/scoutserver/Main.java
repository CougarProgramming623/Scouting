package com.jt.scoutserver;

import java.io.File;
import java.util.Random;

import com.jt.scoutcore.MatchSubmission;
import com.jt.scoutcore.ScoutingConstants;
import com.jt.scoutcore.ScoutingUtils;
import com.jt.scoutcore.TeamColor;
import com.jt.scoutserver.utils.SystemUtils;

public class Main {

	public static void main(String[] args) throws Exception {
		/*
		Random r = new Random();
		for (int i = 10; i < 20; i++) {
			MatchSubmission sub = new MatchSubmission(369, 1, TeamColor.BLUE);
			sub.put("Test1", r.nextInt(100));
			sub.put("Test2", r.nextBoolean());
			sub.put("Test3", r.nextDouble());
			sub.put("Test4", r.nextInt());
			sub.put("Test5", r.nextInt());
			sub.put("Test6", r.nextInt());
			sub.put("Test7", r.nextInt());

			ScoutingUtils.write(sub, new File(i + "." + ScoutingConstants.EXTENSION));
		}

		System.exit(0);*/
		
		
		
		Server server = new Server();
		while (true) {
			if (SystemUtils.hasNewDevices()) {
				System.out.println("Detected new device! Press \"pull\" to pull new files");
				Thread.sleep(1000);
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

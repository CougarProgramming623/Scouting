package com.jt.scoutserver.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class SystemUtils {

	private static final String EXCEL_EXE_NAME = "EXCEL.EXE";

	public static boolean isExcelRunning() {
		try {
			return isProcessRunning(EXCEL_EXE_NAME);
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isProcessRunning(String processName) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder("tasklist.exe");
		Process process = processBuilder.start();
		String tasksList = toString(process.getInputStream());
		return tasksList.contains(processName);
	}

	public static String toString(InputStream inputStream) {
		Scanner scanner = new Scanner(inputStream);
		scanner.useDelimiter("\\A");
		String string = scanner.hasNext() ? scanner.next() : "";
		scanner.close();

		return string;
	}
}

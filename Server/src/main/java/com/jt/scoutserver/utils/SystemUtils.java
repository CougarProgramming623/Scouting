package com.jt.scoutserver.utils;

import java.io.IOException;
import java.io.InputStream;

public class SystemUtils {

	private static final String EXCEL_EXE_NAME = "EXCEL.EXE";
	
	static {
		System.loadLibrary("Scouting App Natives");
		
		nativeInit();
	}

	/**
	 * Returns if a new device has been plugged into this system since the last time this method was called - or when this class was initialized
	 * 
	 */
	public static native boolean hasNewDevices();
	
	private static native int nativeInit();
	
	public static native void nativeExit();

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
		StringBuilder sb = new StringBuilder();
		int b;
		while (true) {
			try {
				b = inputStream.read();
				if (b == -1)
					break;
				sb.append((char) b);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return sb.toString();
	}
}

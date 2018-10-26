package com.jt.scoutserver.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.poi.util.IOUtils;

public class SystemUtils {

	private static final String EXCEL_EXE_NAME = "EXCEL.EXE";

	static {
		System.out.println("Loading native library");
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

	public static void run(String string, String input) {
		ProcessBuilder builder = new ProcessBuilder(string.split(" "));
		try {
			builder.start();
			Process p = builder.start();
			p.getOutputStream().write(input.getBytes(Charset.forName("UTF-8")));
			p.waitFor();
			System.out.println("Out:::");
			IOUtils.copy(p.getInputStream(), System.out);
		} catch (Exception e) {
			System.out.println("Error running \"" + string + "\"");
		}

	}
}

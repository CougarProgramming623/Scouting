package com.jt.scoutserver.utils;

import java.io.File;
import java.io.IOException;

public class AndroidUtils {
	public static final String UNABLE_TO_RUN_FIND_ADB = "ADB_NOT_FOUND", ADB_NO_DEVICES_FOUND = "ADB_NO_DEVICES_FOUND";
	
	public static String getFile(String deviceDir, String fileName, File resultingDir) {
		ProcessBuilder processBuilder = new ProcessBuilder("adb/adb", "pull",
				"\"" + deviceDir + File.separatorChar + fileName + "\"",
				"\"" + new File(resultingDir, fileName) + "\"");
		// processBuilder.directory(new File("./adb/"));
		System.out.println(processBuilder.command());
		Process process;
		try {
			process = processBuilder.start();
			int code = process.waitFor();
			String output = SystemUtils.toString(process.getInputStream());
			if (code != 0) {
				Utils.showError("Error", "ADB failed!\n" + output);
				return ADB_NO_DEVICES_FOUND;
			}
			return output;
		} catch (IOException e) {
			Utils.showError("Error", "Unable to start adb shell\n" + e.getClass() + ": " + e.getMessage());
			return UNABLE_TO_RUN_FIND_ADB;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}

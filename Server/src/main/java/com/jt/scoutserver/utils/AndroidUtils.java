package com.jt.scoutserver.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AndroidUtils {
	public static final String UNABLE_TO_RUN_FIND_ADB = "ADB_EXE_NOT_FOUND", ADB_ERROR = "ADB_NO_DEVICES_FOUND";

	public static String getFile(String deviceDir, String fileName, File resultingDir) {
		return runAdbShell("pull", "\"" + deviceDir + File.separatorChar + fileName + "\"", "\"" + new File(resultingDir, fileName) + "\"");
	}

	public static String deleteFile(String file) {
		return runAdbShell("rm", "-f", file);
	}

	public static String[] listFiles(String folder) {
		String result = runAdbShell("ls", "-1", folder);
		if (result == UNABLE_TO_RUN_FIND_ADB)
			return new String[] { result };
		if (result == ADB_ERROR)
			return new String[] { result };
		ArrayList<String> list = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new StringReader(result));
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				list.add(line);
			}
		} catch (IOException e) {
			// Ignore. String reader will never fail
		}
		String[] finalResult = new String[list.size()];
		list.toArray(finalResult);
		return finalResult;
	}

	private static String runAdbShell(String... commands) {
		try {

			List<String> args = new ArrayList<String>();
			args.add("adb");
			for (String element : commands) {
				args.add(element);
			}
			ProcessBuilder builder = new ProcessBuilder(args);
			File file = new File("./adb");
			System.out.println(file.exists());
			System.out.println(Arrays.toString(file.list()));
			System.out.println(file.getAbsolutePath());
			builder.directory(file);
			Process process = builder.start();
			System.out.println(args);
			int code = process.waitFor();
			String output = SystemUtils.toString(process.getInputStream());
			if (code != 0) {
				Utils.showError("Error", "ADB failed!\n" + output);
				return ADB_ERROR;
			}
			return output;
		} catch (IOException e) {
			Utils.showError("Error", "Unable to start adb shell\nCommand:" + Arrays.toString(commands) + "\n" + e.getClass() + ": " + e.getMessage());
			return UNABLE_TO_RUN_FIND_ADB;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}

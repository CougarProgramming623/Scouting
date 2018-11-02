package com.jt.scoutcore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class ScoutingUtils {
	
	public static String getSaveDir() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("./path.txt")));
			String line = reader.readLine();
			reader.close();
			return line;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T read(File file, Class<T> type) {
		try {
			Input in = new Input(new FileInputStream(file));
			Object obj = ScoutingConstants.KRYO.get().readClassAndObject(in);
			assert type.isAssignableFrom(obj.getClass());
			return (T) obj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void write(File file, Object object, Class<T> type) {
		assert type.isAssignableFrom(object.getClass());
		try {
			Output out = new Output(new FileOutputStream(file));
			ScoutingConstants.KRYO.get().writeClassAndObject(out, object);
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static MatchSubmission read(File file) {
		return read(file, MatchSubmission.class);
	}

	public static void write(MatchSubmission sub, File file) {
		write(file, sub, MatchSubmission.class);
	}

	public static String makeLength(String string, int length) {
		if (string.length() == length)
			return string;

		if (string.length() > length)
			return string.substring(0, length);
		char[] chars = new char[length];
		string.getChars(0, string.length(), chars, 0);
		for (int i = string.length(); i < length; i++) {
			chars[i] = ' ';
		}
		return new String(chars);
	}
}

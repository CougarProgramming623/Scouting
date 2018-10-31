package com.jt.scoutcore;

import java.util.ArrayList;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;

public class ScoutingConstants {

	public static final String FOLDER_NAME = "JT Robo App";
	//format:off
	public static final String ANDROID_SAVE_DIRECTORY1 = "/sdcard/Android/data/com.jt.scoutingapp/files/", 
			ANDROID_MATCHES_SAVE_DIRECTORY = ANDROID_SAVE_DIRECTORY1 + "matches/", EXTENSION = "jtm", 
			ANDROID_ASSIGNMENTS_FILE_NAME = "assignments.jt", ANDROID_ASSIGNMENTS_FILE = ANDROID_SAVE_DIRECTORY1 + ANDROID_ASSIGNMENTS_FILE_NAME;
	//format:on

	@SuppressWarnings("rawtypes")
	public static final Class<HashMap> MAP_CLASS = HashMap.class;

	public static final ThreadLocal<Kryo> KRYO1 = new ThreadLocal<Kryo>() {
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();
			kryo.register(MatchSubmission.class);
			kryo.register(HashMap.class);
			kryo.register(TeamColor.class);
			kryo.register(ArrayList.class);
			kryo.register(AssignerEntry.class);
			return kryo;
		};
	};
}

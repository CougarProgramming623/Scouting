package com.jt.scoutcore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.esotericsoftware.kryo.Kryo;

public class ScoutingConstants {

	public static final String FOLDER_NAME = "JT Robo App";
	//public static final String ANDROID_SAVE_DIRECTORY1 = s + "storage" + s + "emulated" + s + "0" + s + "Android" + s + "data" + s + "com.jt.scoutingapp" + s + "files" + s;
	public static final String ANDROID_MATCHES_SAVE_DIRECTORY_NAME = "matches" + '/', EXTENSION = "jtm";
	public static final String ANDROID_ASSIGNMENTS_FILE_NAME = "assignments.jt";

	@SuppressWarnings("rawtypes")
	public static final Class<HashMap> MAP_CLASS = HashMap.class;
	

	public static final ThreadLocal<Kryo> KRYO = new ThreadLocal<Kryo>() {
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();
			kryo.register(MatchSubmission.class);
			kryo.register(LinkedHashMap.class);
			kryo.register(TeamColor.class);
			kryo.register(ArrayList.class);
			kryo.register(AssignerList.class);
			kryo.register(AssignerEntry.class);
			return kryo;
		};
	};
}

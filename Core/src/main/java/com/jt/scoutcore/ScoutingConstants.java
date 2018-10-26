package com.jt.scoutcore;

import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;

public class ScoutingConstants {

	public static final String FOLDER_NAME = "JT Robo App";
	public static final String ANDROID_SAVE_DIRECTORY = "/sdcard/" + FOLDER_NAME, EXTENSION = "jtm";

	@SuppressWarnings("rawtypes")
	public static final Class<HashMap> MAP_CLASS = HashMap.class;

	public static final ThreadLocal<Kryo> KRYO = new ThreadLocal<Kryo>() {
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();
			kryo.register(MatchSubmission.class);
			kryo.register(HashMap.class);
			kryo.register(TeamColor.class);
			return kryo;
		};
	};
}

package com.jt.scoutcore;

import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;

public class ScoutingConstants {
	@SuppressWarnings("rawtypes")
	public static final Class<HashMap> MAP_CLASS = HashMap.class;
	
	public static final ThreadLocal<Kryo> KRYO = new ThreadLocal<Kryo>() {
		protected Kryo initialValue() {
			return new Kryo();
		};
	};
}

package com.jt.scoutcore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

import com.esotericsoftware.kryo.io.Input;

public class Submission {
	private HashMap<String, Object> map;
	
	//For serialization
	public Submission() {
		
	}

	public void add(String name, double amount) {
		Double value = get(name, Double.class);
		map.put(name, value + amount);
	}
	
	public void put(String name, Object value) {
		map.put(Objects.requireNonNull(name), value);
	}
	
	@SuppressWarnings("unchecked")
	private <T> T get(String name, Class<T> type) {
		Object obj = map.get(name);
		if(obj == null) throw new NullPointerException("Null values not allowed! Key: " + name);
		assert(type.isAssignableFrom(obj.getClass()));
		return (T) obj;
	}

	public Submission(HashMap<String, Object> map) {
		this.map = map;
	}
	
	public Submission(File file) throws FileNotFoundException {
		this(new FileInputStream(file));
	}
	
	@SuppressWarnings("unchecked")
	public Submission(InputStream stream) {
		Input in = new Input(stream);
		map = ScoutingConstants.KRYO.get().readObject(in, ScoutingConstants.MAP_CLASS);
		in.close();
	}
	
	
	public HashMap<String, Object> getMap() {
		return map;
	}
	
}

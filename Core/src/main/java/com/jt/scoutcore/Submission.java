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

	// For serialization
	public Submission() {

	}

	public void add(String name, double amount) {
		Double value;
		if (map.containsKey(name))
			value = get(name, Double.class);
		else
			value = Double.valueOf(0.0);
		map.put(name, Double.valueOf(value + amount));
	}

	public void put(String name, Object value) {
		if (value instanceof Number && !(value instanceof Double))//Convert all types of numbers to a double
			value = Double.valueOf(((Double) value).doubleValue());
		map.put(Objects.requireNonNull(name), value);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String name, Class<T> type) {
		Object obj = map.get(name);
		if (obj == null)
			throw new NullPointerException("Null values not allowed! Key: " + name);
		assert (type.isAssignableFrom(obj.getClass()));
		return (T) obj;
	}

	public Object get(String name) {
		return map.get(name);
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

package com.jt.scoutcore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

import com.esotericsoftware.kryo.io.Input;

public class MatchSubmission {

	private static final String TEAM_NUMBER_KEY = "Team", MATCH_NUMBER_KEY = "Match", COLOR_KEY = "Color";

	private HashMap<String, Object> map;

	// For serialization
	public MatchSubmission() {
	}

	public MatchSubmission(int teamNumber, int matchNumber, TeamColor color) {
		setTeamNumber(teamNumber);
		setMatchNumber(matchNumber);
		setColor(color);
	}

	public void add(String name, double amount) {
		Double value;
		if (map.containsKey(name))
			value = get(name, Double.class);
		else
			value = Double.valueOf(0.0);
		map.put(name, Double.valueOf(value + amount));
	}

	public int getTeamNumber() {
		Integer num = get(TEAM_NUMBER_KEY, Integer.class);
		if (num == null)
			return -1;
		return num.intValue();
	}

	public void setTeamNumber(int number) {
		map.put(TEAM_NUMBER_KEY, Integer.valueOf(number));
	}

	public int getMatchNumber() {
		Integer num = get(MATCH_NUMBER_KEY, Integer.class);
		if (num == null)
			return -1;
		return num.intValue();
	}

	public void setMatchNumber(int number) {
		map.put(MATCH_NUMBER_KEY, Integer.valueOf(number));
	}

	public void setColor(TeamColor color) {
		map.put(COLOR_KEY, color);
	}

	public TeamColor getColor() {
		return get(COLOR_KEY, TeamColor.class);
	}

	public void put(String name, Object value) {
		if (value instanceof Number && !(value instanceof Double))// Convert all types of numbers to a double
			value = Double.valueOf(((Double) value).doubleValue());
		map.put(Objects.requireNonNull(name), value);
	}

	public Class<?> getType(String name) {
		Object obj = map.get(name);
		if (obj == null)
			return null;
		return obj.getClass();
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

	public MatchSubmission(HashMap<String, Object> map) {
		this.map = map;
	}

	public MatchSubmission(File file) throws FileNotFoundException {
		this(new FileInputStream(file));
	}

	@SuppressWarnings("unchecked")
	public MatchSubmission(InputStream stream) {
		Input in = new Input(stream);
		map = ScoutingConstants.KRYO.get().readObject(in, ScoutingConstants.MAP_CLASS);
		in.close();
	}

	public HashMap<String, Object> getMap() {
		return map;
	}

}

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
		map = new HashMap<String, Object>();
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

	public void add(String name, int amount) {
		Integer value;
		if (map.containsKey(name))
			value = get(name, Integer.class);
		else
			value = Integer.valueOf(0);
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
		map.put(Objects.requireNonNull(name), Objects.requireNonNull(value));
	}

	public Class<?> getType(String name) {
		Object obj = map.get(name);
		if (obj == null)
			return null;
		return obj.getClass();
	}

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

	public HashMap<String, Object> getMap() {
		return map;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MatchSubmission other = (MatchSubmission) obj;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Team: " + ScoutingUtils.makeLength(Integer.toString(getTeamNumber()), 5) + " Match#: " + ScoutingUtils.makeLength(Integer.toString(getMatchNumber()), 4) + " Color: " + getColor();
	}

	public boolean has(String attribute) {
		return map.containsKey(attribute);
	}

}

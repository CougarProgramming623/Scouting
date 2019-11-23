package com.jt.scoutcore;

public class AssignerEntry {
	public int match, team;
	public boolean red;

	public AssignerEntry() {

    }

	public AssignerEntry(int match, int team, boolean red) {
		this.match = match;
		this.team = team;
		this.red = red;
	}

	@Override
	public String toString() {
		return "AssignerEntry [match=" + match + ", team=" + team + ", red=" + red + "]";
	}

}

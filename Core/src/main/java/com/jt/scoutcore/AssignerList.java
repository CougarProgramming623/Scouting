package com.jt.scoutcore;

import java.util.ArrayList;

public class AssignerList {
	public ArrayList<AssignerEntry> entries = new ArrayList<AssignerEntry>();
	public int matchStart = 0;
	
	public AssignerList(ArrayList<AssignerEntry> entries, int matchStart) {
		this.entries = entries;
		this.matchStart = matchStart;
	}

}

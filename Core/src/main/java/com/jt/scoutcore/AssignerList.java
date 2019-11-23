package com.jt.scoutcore;

import java.util.ArrayList;
import java.util.List;

public class AssignerList extends AssignmentsBase {
	private ArrayList<AssignerEntry> entries = new ArrayList<AssignerEntry>();
	private int index = -1;

	public AssignerList() {

	}

	public AssignerList(ArrayList<AssignerEntry> entries, int matchStart) {
		this.entries = entries;
		if(matchStart == 0) {
            this.index = 0;
        } else {
            for(int i = 0; i < entries.size(); i++) {
                if(entries.get(i).match == matchStart) {
                    this.index = i;
                    break;
                }
            }
        }
	}

	public AssignerEntry getCurrent() {
	    if(index <= -1) return null;
	    if(index >= entries.size()) return null;
		return entries.get(index);
	}

    public void next(OnMatchDetermined consumer) {
        index++;
        consumer.submit(getCurrent());
    }

    public boolean hasAssignment() {
        return index >= 0 && index < entries.size();
    }

    public AssignerEntry goToMatch(int match, int team) {
        for(int i = 0; i < entries.size(); i++) {
            AssignerEntry entry = entries.get(i);
            if(entry.match == match) {
                if(entry.team == team || team == -1) {
                    index = i;
                    return entry;
                }
            }
        }
        return null;
    }


    public List<AssignerEntry> getRemainingAssignments() {
        List<AssignerEntry> result = new ArrayList<AssignerEntry>();
        for(int i = index + 1; i < entries.size(); i++) {
            result.add(entries.get(i));
        }
        return result;
    }

    public List<AssignerEntry> getAllAssignments() {
        return new ArrayList<>(entries);
    }


    public AssignerEntry goToMatch(int match) {
        return goToMatch(match, -1);
    }


    public boolean hasDynamicAssignments() {
	    return false;
    }

}

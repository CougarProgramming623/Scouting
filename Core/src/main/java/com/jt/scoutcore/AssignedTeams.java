package com.jt.scoutcore;

import com.esotericsoftware.minlog.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssignedTeams extends AssignmentsBase {

	public interface MatchCreator {
		public void getNext(int[] teams, OnMatchDetermined listener);
	}

	private MatchCreator creator = null;
	private AssignerEntry current = null;
	private List<AssignerEntry> submittedMatches;
	private int[] teams;
	
	public AssignedTeams(int[] teams) {
		this.teams = teams;
		this.submittedMatches = new ArrayList<AssignerEntry>();
	}

	public AssignedTeams() {
		submittedMatches = new ArrayList<AssignerEntry>();
	}

	@Override
	public AssignerEntry getCurrent() {
		return current;
	}

	@Override
	public void next(OnMatchDetermined consumer) {
	    if(submittedMatches == null)
	        submittedMatches = new ArrayList<AssignerEntry>();
	    if(current != null)
            submittedMatches.add(current);
	    try {
            throw new RuntimeException();
        } catch (Exception e) {
	        Log.warn("Scout", "Call to next()");
	        for(StackTraceElement element : e.getStackTrace()) {
                Log.warn("Scout", element.toString());
            }

        }
        if (creator != null) {
            creator.getNext(teams, (assignment) -> {
                current = assignment;
                if(consumer != null) consumer.submit(assignment);
            });
        }
	}

	@Override
	public AssignerEntry goToMatch(int match, int team) {
	    if(submittedMatches == null) return null;
		for (AssignerEntry entry : submittedMatches) {
			if (entry.match == match && entry.team == team) {
				return current = entry;
			}
		}
		return null;
	}

	@Override
	public List<AssignerEntry> getRemainingAssignments() {
		return new ArrayList<AssignerEntry>();
	}

	@Override
	public List<AssignerEntry> getAllAssignments() {
	    if(submittedMatches == null) return new ArrayList<AssignerEntry>();
		else return new ArrayList<AssignerEntry>(submittedMatches);
	}

	@Override
	public boolean hasDynamicAssignments() {
		return true;
	}

	public void setCreator(MatchCreator creator) {
		this.creator = creator;
	}

    public int[] getTeams() {
        return teams;
    }

    @Override
	public String toString() {
		return "AssignedTeams [teams=" + Arrays.toString(teams) + "]";
	}
	
	

}

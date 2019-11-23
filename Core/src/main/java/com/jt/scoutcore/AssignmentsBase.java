package com.jt.scoutcore;

import java.util.List;

public abstract class AssignmentsBase {

    public interface OnMatchDetermined {
        public void submit(AssignerEntry submission);
    }

    /**
     * Returns the current assignment without changing what it is
     */
    public abstract AssignerEntry getCurrent();



    /**
     * Skips on to the next assignment
     */
    public abstract void next(OnMatchDetermined consumer);

    /**
     * Sets the current assignment to be for match match and team team and retuens that assignment, or null if no such assignment exists
     * If null is returned it means that no matching assignment and current was not changed
     * @param match The match to look for
     * @param team The team to look for or -1 if no specific team needs to be targeted
     * @return The assignment for the desired team, match combo or null if no such combo exists, or if team is -1 and mutiple matches exist for the specified match number
     */
    public abstract AssignerEntry goToMatch(int match, int team);

    /**
     * Returns the remaining assignments from the next match to the last assigned match.
     */
    public abstract List<AssignerEntry> getRemainingAssignments();

    /**
     * Returns all assignments, including all completed, the current one, and any future assignments
     * @return
     */
    public abstract List<AssignerEntry> getAllAssignments();

    /**
     * Skips to the match number specified and retuens it. Null is returned if no such match exists.
     * If mutiple matches with the same number exist then this returns the first one and calls to next will move on to the next one.
     * @param match
     * @return
     */
    public AssignerEntry goToMatch(int match) {
        return goToMatch(match, -1);
    }

    /**
     * Returns true if new assignments have been added since the last call to getCurrent or next
     */
    public abstract boolean hasDynamicAssignments();

    /**
     * Returns true if this device has an assignment. This indicates that calls to getCurrent() will return valid assignments (non-null)
     * @return
     */
    public boolean hasAssignment() {
        return getCurrent() != null;
    }

}

package edu.uiuc.cs.cs425.mp1.util;

import java.util.Collections;
import java.util.List;

public class ServerUtils {

    /**
     * Returns list of process ids a given process must connect to during initialization.
     * Note: Assumes that {@param ids} is sorted.
     */
    public static List<Integer> getTargetProcesses(int assignedId, List<Integer> ids) {
        int index = ids.indexOf(assignedId);
        if(index != -1) {
            return ids.subList(index + 1, ids.size());
        }
        return Collections.emptyList();
    }

}

package edu.uiuc.cs.cs425.mp1.util;

import edu.uiuc.cs.cs425.mp1.config.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ServerUtils {

    private static final Random random = new Random();

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

    public static void incrementMap(int key, Map<Integer, Integer> map) {
        map.put(key, map.get(key) + 1);
    }

    public static int getRandomNetworkDelay() {
        int minDelay = Configuration.INSTANCE.getMinDelay();
        int maxDelay = Configuration.INSTANCE.getMaxDelay();
        return randomRange(minDelay, maxDelay);
    }

    public static int randomRange(int min, int max) {
        return random.nextInt(max + 1 - min) + min;
    }

}

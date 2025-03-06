package com.exaroton.api.server;

import java.util.Set;

public final class PlayerInfo {
    /**
     * Maximum player count (slots)
     */
    private final int max;

    /**
     * Current player count
     */
    private final int count;

    /**
     * Current player list (not always available)
     */
    private final Set<String> list;


    public PlayerInfo(int max, int count, Set<String> list) {
        this.max = max;
        this.count = count;
        this.list = list;
    }

    /**
     * Get the maximum player count
     * @return maximum player count
     */
    public int getMax() {
        return max;
    }

    /**
     * Get the current player count
     * @return current player count
     */
    public int getCount() {
        return count;
    }

    /**
     * Get the current player list. This might not be available depending on the server software and version.
     * @return current player list
     */
    public Set<String> getList() {
        return list;
    }
}

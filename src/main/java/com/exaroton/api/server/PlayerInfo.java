package com.exaroton.api.server;

import java.util.Set;

@SuppressWarnings("unused")
public final class PlayerInfo {
    /**
     * Maximum player count (slots)
     */
    private int max;

    /**
     * Current player count
     */
    private int count;

    /**
     * Current player list (not always available)
     */
    private Set<String> list;

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

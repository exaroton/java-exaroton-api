package com.exaroton.api.server;

public class PlayerInfo {
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
    private final String[] list;


    public PlayerInfo(int max, int count, String[] list) {
        this.max = max;
        this.count = count;
        this.list = list;
    }


    public int getMax() {
        return max;
    }

    public int getCount() {
        return count;
    }

    public String[] getList() {
        return list;
    }
}

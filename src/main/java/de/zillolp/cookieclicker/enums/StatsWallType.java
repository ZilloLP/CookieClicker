package de.zillolp.cookieclicker.enums;

import de.zillolp.cookieclicker.statswall.statswalls.StatsWall;

public enum StatsWallType {
    TIME("Time"),
    ALLTIME("Alltime");

    private final String displayName;
    private final String path;
    private StatsWall statsWall;

    StatsWallType(String displayName) {
        this.displayName = displayName;
        this.path = "locations.StatsWalls." + displayName;
    }

    public StatsWall getStatsWall() {
        return statsWall;
    }

    public void setStatsWall(StatsWall statsWall) {
        this.statsWall = statsWall;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPath() {
        return path;
    }
}

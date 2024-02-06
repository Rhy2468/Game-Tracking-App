package ca.cmpt276.coopachievement.model;

/**
 * Manages icon, minimum score, and level name for each tier of achievement
 */

public class AchievementManager {
    private final String level;
    private final int minScore;
    private final int iconID;

    public AchievementManager(String level, int minScore, int iconID) {
        this.level = level;
        this.minScore = minScore;
        this.iconID = iconID;
    }

    public String getLevel() {
        return level;
    }

    public int getMinScore() {
        return minScore;
    }

    public int getIconID() {
        return iconID;
    }

}

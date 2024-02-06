package ca.cmpt276.coopachievement.model;

/**
 * Manages individual achievement names/values
 */

public enum AchievementLevels {
    ACHIEVEMENT_LEVEL1("7"),
    ACHIEVEMENT_LEVEL2("6"),
    ACHIEVEMENT_LEVEL3("5"),
    ACHIEVEMENT_LEVEL4("4"),
    ACHIEVEMENT_LEVEL5("3"),
    ACHIEVEMENT_LEVEL6("2"),
    ACHIEVEMENT_LEVEL7("1"),
    ACHIEVEMENT_LEVEL8("0");

    private final String value;
    public static final int NUM_LEVELS = 8;
    private final String [][] achievementNames = {
            {"Amazing Amys", "Bold Bettys", "Capable Calebs", "Dynamic Dereks", "Erratic Edgars", "Forgivable Felixs", "Greasy Graces", "Horrid Harrys"},
            {"Terrific Tiger", "Crazy Cheetah", "Lovable Lion", "Beastly Bear", "Proud Panda", "Pretty Pig", "Bold Bunny", "Mighty Mouse"},
            {"*Imagination*", "Secret Formula", "Aye-Aye Captin!", "I'm Ready!", "Order Up!", "Tartar Sauce", "Barnacles!", "Gary the Snail"}};

    AchievementLevels(final String value) {
        this.value = value;
    }


    public String getValue() {
        int themeIndex = GameTypeManager.getThemeIndex();

        if (themeIndex != 0){
            themeIndex--;
        }

        return achievementNames[themeIndex][Integer.parseInt(value)];
    }

    public String getLevel(){
        return value;
    }
}

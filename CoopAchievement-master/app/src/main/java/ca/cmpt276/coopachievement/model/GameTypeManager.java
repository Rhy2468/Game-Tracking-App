package ca.cmpt276.coopachievement.model;

import java.util.ArrayList;

/**
 * Manages each game type and is used to pass game data between activities
 */

public class GameTypeManager {
    final ArrayList<GameType> gameTypeArray = new ArrayList<>();
    private static GameTypeManager instance;
    private static int themeIndex = 0;
    public static int getThemeIndex() {
        return themeIndex;
    }

    public static void setThemeIndex(int themeIndex) {
        GameTypeManager.themeIndex = themeIndex;
    }

    public static GameTypeManager getInstance() {
        if (instance == null) {
            instance = new GameTypeManager();
        }
        return instance;
    }

    public static void setInstance(GameTypeManager gameTypeManager) {
        instance = gameTypeManager;
    }

    public int size() {
        return gameTypeArray.size();
    }
    public void addGame(GameType gameType) {
        gameTypeArray.add(gameType);
    }
    public void deleteGame(int index) {
        gameTypeArray.remove(index);
    }
    public GameType get(int index) {
        return gameTypeArray.get(index);
    }
    public String determineThemeSelected() {
        if (themeIndex == 1){
            return "c";
        } else if (themeIndex == 2){
            return "animal";
        } else if (themeIndex == 3){
            return "sp";
        }

        return "c";
    }
}

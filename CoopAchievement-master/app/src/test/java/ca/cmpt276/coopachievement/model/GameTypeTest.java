package ca.cmpt276.coopachievement.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GameTypeTest {

    @Test
    void deleteGame() {
        GameType gameType = new GameType();
        gameType.addGame(new Game(1,0,AchievementLevels.ACHIEVEMENT_LEVEL1, 0, 0, "normal"));
        assertEquals(1, gameType.size());
        gameType.deleteGame(0);
        assertEquals(0, gameType.size());
    }

    @Test
    void get() {
        GameType gameType = new GameType();
        Game game = new Game(1,0,AchievementLevels.ACHIEVEMENT_LEVEL1, 0, 0, "normal");
        gameType.addGame(game);
        Game gameRetrieved = gameType.get(0);
        assertEquals(game, gameRetrieved);
    }

    @Test
    void emptySize() {
        GameType gameType = new GameType();
        assertEquals(0, gameType.size());
    }

    @Test
    void size1() {
        GameType gameType = new GameType();
        gameType.addGame(new Game(1,0,AchievementLevels.ACHIEVEMENT_LEVEL1, 0, 0, "normal"));
        assertEquals(1, gameType.size());
    }

    @Test
    void size100() {
        GameType gameType = new GameType();
        for (int i = 0; i < 100; i++) {
            gameType.addGame(new Game(1,0,AchievementLevels.ACHIEVEMENT_LEVEL1, 0, 0, "normal"));
        }
        assertEquals(100, gameType.size());
    }

    @Test
    void addGame() {
        GameType gameType = new GameType();
        assertEquals(0, gameType.size());
        gameType.addGame(new Game(1,0,AchievementLevels.ACHIEVEMENT_LEVEL1, 0, 0, "normal"));
        assertEquals(1, gameType.size());
    }

    @Test
    void getName() {
        GameType gameType = new GameType();
        gameType.setName("test");
        assertEquals("test", gameType.getName());
    }

    @Test
    void getImage() {
        GameType gameType = new GameType();
        gameType.setImageSelectedIndex(1);
        assertEquals(1, gameType.getImageSelectedIndex());
    }

    @Test
    void getPoorScore() {
        GameType gameType = new GameType();
        gameType.setExpectedPoorScore(0);
        assertEquals(0, gameType.getExpectedPoorScore());
    }

    @Test
    void getGreatScore() {
        GameType gameType = new GameType();
        gameType.setExpectedGreatScore(10);
        assertEquals(10, gameType.getExpectedGreatScore());
    }
}

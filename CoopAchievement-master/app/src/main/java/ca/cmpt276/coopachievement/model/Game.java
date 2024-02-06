package ca.cmpt276.coopachievement.model;


import static java.time.LocalDateTime.now;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ArrayList;

/**
 * Handles individual game logic including data and calculating achievement level
 */

public class Game {
    private AchievementLevels achievementLevel;
    private int numPlayer;
    private int sumScores;
    private final ArrayList<Integer> playerScores = new ArrayList<>();
    private final ArrayList<Integer> prevPlayerScores = new ArrayList<>();
    private final String creationDate;
    private int expectedPoorScore;
    private int expectedGreatScore;
    private String difficulty;
    private String encodedPhoto;


    public Game(int numPlayer, int sumScores, AchievementLevels achievement, int expectedPoorScore, int expectedGreatScore, String difficultyLevel) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("LLL d @ h:mm a");
        this.creationDate = now().format(formatter);
        this.numPlayer = numPlayer;
        this.sumScores = sumScores;
        this.achievementLevel = achievement;
        this.expectedPoorScore = expectedPoorScore;
        this.expectedGreatScore = expectedGreatScore;
        this.difficulty = difficultyLevel;
    }
    public AchievementLevels getAchievementLevel() {
        return achievementLevel;
    }


    public String getCreationDate() {
        return creationDate;
    }
    public int getNumPlayer() {
        return numPlayer;
    }
    public int getSumScores() {
        return sumScores;
    }
    public String getDifficulty() {return difficulty;}
    public ArrayList<Integer> getPlayerScores() { return playerScores; }
    public ArrayList<Integer> getPrevPlayerScores() { return prevPlayerScores; }

    public Bitmap getGamePhoto() {
        byte[] imageAsBytes = Base64.decode(encodedPhoto.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
    public String getEncodedPhoto() {
        return encodedPhoto;
    }

    public void setExpectedPoorScore(int expectedPoorScore) {
        this.expectedPoorScore = expectedPoorScore;
    }
    public void setExpectedGreatScore(int expectedGreatScore) {
        this.expectedGreatScore = expectedGreatScore;
    }
    public void setAchievementLevel(AchievementLevels achievementLevel) {
        this.achievementLevel = achievementLevel;
    }
    public void setNumPlayer(int numPlayer) {
        this.numPlayer = numPlayer;

    }
    public void setSumScores(int sumScores) {
        this.sumScores = sumScores;

    }
    public void setDifficulty (String difficultyLevel) {
        this.difficulty = difficultyLevel;
    }
    public void setPlayerScores(ArrayList<Integer> playerScores) {
        this.playerScores.clear();
        this.playerScores.addAll(playerScores);
    }
    public void setGamePhoto(Bitmap photoTaken) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photoTaken.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        this.encodedPhoto = Base64.encodeToString(b, Base64.DEFAULT);
    }


    public int tierIncrement(int pos) {
        double difficultyMultiplier = 1;
        if (Objects.equals(this.difficulty, "Easy")) {
            difficultyMultiplier = 0.75;
        } else if (Objects.equals(this.difficulty, "Hard")) {
            difficultyMultiplier = 1.25;
        }
        if (pos <= 1) {
            return 0;
        } else if (pos == 2) {
            return (int) (expectedPoorScore * difficultyMultiplier);
        } else {
            float increment_float = (pos-2) * ((float)expectedGreatScore - (float)expectedPoorScore)/
                    (float)(AchievementLevels.NUM_LEVELS-2);
            if (increment_float > expectedGreatScore) {
                return (int) (expectedGreatScore * difficultyMultiplier);
            } else {
                int roundedIncrement = (int) Math.ceil(increment_float);
                return (int) ((expectedPoorScore + roundedIncrement) * difficultyMultiplier);
            }
        }
    }

    public int calculateSumScore() {
        sumScores = 0;
        for (int score : playerScores) {
            sumScores += score;
        }
        return sumScores;
    }

    public AchievementLevels calculateTier() {
        int gameScore = this.sumScores/this.numPlayer;

        if (gameScore < tierIncrement(2)) {
            return AchievementLevels.ACHIEVEMENT_LEVEL1;
        } else if (tierIncrement(2) <= gameScore && gameScore < tierIncrement(3)) {
            return AchievementLevels.ACHIEVEMENT_LEVEL2;
        } else if (tierIncrement(3) <= gameScore && gameScore < tierIncrement(4)) {
            return AchievementLevels.ACHIEVEMENT_LEVEL3;
        } else if (tierIncrement(4) <= gameScore && gameScore < tierIncrement(5)) {
            return AchievementLevels.ACHIEVEMENT_LEVEL4;
        } else if (tierIncrement(5) <= gameScore && gameScore < tierIncrement(6)) {
            return AchievementLevels.ACHIEVEMENT_LEVEL5;
        } else if (tierIncrement(6) <= gameScore && gameScore < tierIncrement(7)) {
            return AchievementLevels.ACHIEVEMENT_LEVEL6;
        } else if (tierIncrement(7) <= gameScore && gameScore < tierIncrement(8)) {
            return AchievementLevels.ACHIEVEMENT_LEVEL7;
        } else {
            return AchievementLevels.ACHIEVEMENT_LEVEL8;
        }
    }
}

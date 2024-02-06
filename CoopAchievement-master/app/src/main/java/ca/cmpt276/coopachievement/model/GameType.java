package ca.cmpt276.coopachievement.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Contains data for game types including score expectations, icon, and name
 * Also logic for calculating increment/value between and for achievement levels
 */

public class GameType {
    private final ArrayList<Game> gameArray = new ArrayList<>();
    private String name;
    private int imageSelectedIndex;
    private int expectedPoorScore;
    private int expectedGreatScore;
    private final int[] ranksEarned = new int[8];
    private String encodedPhoto;

    public GameType() {}

    public int size() {
        return gameArray.size();
    }
    public void addGame(Game game) {
        gameArray.add(game);
    }
    public void deleteGame(int index) {
        gameArray.remove(index);
    }
    public Game get(int index) {
        return gameArray.get(index);
    }
    public Game getLatestGame(){
        return gameArray.get(gameArray.size()-1);
    }

    public String getName() {
        return name;
    }
    public int getImageSelectedIndex() {
        return imageSelectedIndex;
    }
    public int getExpectedPoorScore() {
        return expectedPoorScore;
    }
    public int getExpectedGreatScore() {
        return expectedGreatScore;
    }
    public int[] getRanksEarned() {
        return ranksEarned;
    }

    public Bitmap getPhotoTaken() {
        byte[] imageAsBytes = Base64.decode(encodedPhoto.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setImageSelectedIndex(int imageSelectedIndex) {
        this.imageSelectedIndex = imageSelectedIndex;
    }
    public void setExpectedPoorScore(int expectedPoorScore) {
        this.expectedPoorScore = expectedPoorScore;
    }
    public void setExpectedGreatScore(int expectedGreatScore) {
        this.expectedGreatScore = expectedGreatScore;
    }
    public void setRanksEarned(int pos, int value) {
        ranksEarned[pos] = value;
    }

    public void setPhotoTaken(Bitmap photoTaken) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photoTaken.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        this.encodedPhoto = Base64.encodeToString(b, Base64.DEFAULT);
    }
}

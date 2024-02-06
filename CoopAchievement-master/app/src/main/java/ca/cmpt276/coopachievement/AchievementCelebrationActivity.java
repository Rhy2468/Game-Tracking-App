package ca.cmpt276.coopachievement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import ca.cmpt276.coopachievement.model.Game;
import ca.cmpt276.coopachievement.model.GameType;
import ca.cmpt276.coopachievement.model.GameTypeManager;

/**
 * Handles the achievement celebration page which displays achievement received, score, a re-playable
 * animation, a progress bar to next achievement level, and score needed to achieve next level. Also
 * allows user to change theme by bringing up options page
 */
public class AchievementCelebrationActivity extends AppCompatActivity {

    private static int sumScore;
    private static int numPlayers;
    private TextView scoreReqTitle;
    private ProgressBar progressBar;
    private ImageButton image;
    private int levelIndex;
    private static int rootPos;
    private static int pos;
    private ImageView progressBarAchieved;
    private ImageView progressBarToAchieve;
    private static Game game;
    private static SoundPool soundPool;
    private int audioSound;
    private static final GameTypeManager gameTypeManager = GameTypeManager.getInstance();
    private static final String [][] achievementNames = {
            {"Amazing Amys", "Bold Bettys", "Capable Calebs", "Dynamic Dereks", "Erratic Edgars", "Forgivable Felixs", "Greasy Graces", "Horrid Harrys"},
            {"Terrific Tiger", "Crazy Cheetah", "Lovable Lion", "Beastly Bear", "Proud Panda", "Pretty Pig", "Bold Bunny", "Mighty Mouse"},
            {"*Imagination*", "Secret Formula", "Aye-Aye Captin!", "I'm Ready!", "Order Up!", "Tartar Sauce", "Barnacles!", "Gary the Snail"}};

    public static Intent makeLaunchIntent(Context c, String achievementScore, int rootPos, int pos) {
        Intent intent = new Intent(c, AchievementCelebrationActivity.class);
        intent.putExtra("achievementTitle", achievementScore);
        intent.putExtra("rootPos", rootPos);
        intent.putExtra("pos", pos);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_celebration);

        scoreReqTitle = findViewById(R.id.tvNextAchieveReq);
        progressBar = findViewById(R.id.pbNextAchievement);


        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();


        audioSound = soundPool.load(this, R.raw.clap, 1);

        progressBarAchieved = findViewById(R.id.ivPGAchieved);
        progressBarToAchieve = findViewById(R.id.ivPGToAchieve);

        setUpActionBar();
        getGameData();
        retrieveStoredDataIntent();
        progressBarSetUp();
        changeTheme();
        setAchievementIcon();
        congrats();
        repeatAnimSound();
        displayNextAchievementName();
    }


    @Override
    protected void onStart() {
        super.onStart();
        getGameData();
        setAchievementIcon();
        congrats();
        repeatAnimSound();
        progressBarSetUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return false;
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        actionBar.setTitle(R.string.congratz_title);

        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void getGameData() {
        Bundle bundle = getIntent().getExtras();
        setAchievementTitle(bundle);

        pos = bundle.getInt("pos");
        rootPos = bundle.getInt("rootPos");

        TextView score = findViewById(R.id.tvTeamScore);
        String scoreString = getString(R.string.score) + sumScore;
        score.setText(scoreString);
    }

    private void setAchievementTitle(Bundle bundle) {
        String achievementLevel = bundle.getString("achievementTitle");
        int themeIndex = GameTypeManager.getThemeIndex();

        if (themeIndex != 0){
            themeIndex--;
        }

        levelIndex = Integer.parseInt(achievementLevel);
        String achievementScore = achievementNames[themeIndex][levelIndex];

        TextView title = findViewById(R.id.tvAchievementTitle);
        title.setText(achievementScore);
    }

    private void progressBarSetUp() {
        int achieveIndex = 0;
        progressBar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        while (achieveIndex <= 7){
            int lowInterval = game.tierIncrement(achieveIndex)*numPlayers;
            int highInterval = game.tierIncrement(achieveIndex+1)*numPlayers;
            int highestInterval = game.tierIncrement(8)*numPlayers;

            boolean scoreInIntervalRange = (sumScore >= lowInterval && sumScore < highInterval);
            boolean maxScoreReached = sumScore >= highestInterval;
            if (scoreInIntervalRange){
                int remainder = sumScore - lowInterval;
                float intervalDifference = highInterval - lowInterval;
                int percentageRounded = Math.round((remainder / intervalDifference) * 100);
                int scoreNeeded = (highInterval - lowInterval) - Math.round(remainder);
                String scoreNeededString = getString(R.string.score_to_next_lvl) + scoreNeeded;
                scoreReqTitle.setText(scoreNeededString);
                progressBar.setProgress(percentageRounded);
                setIconImage(progressBarAchieved, levelIndex + 1);
                setIconImage(progressBarToAchieve, levelIndex);
            } else if (maxScoreReached) {
                scoreReqTitle.setText(R.string.max_level_reached);
                progressBar.setProgress(100);


                progressBarAchieved.setVisibility(View.INVISIBLE);
                setIconImage(progressBarToAchieve, 1);
            }
            achieveIndex++;
        }
    }

    private void setIconImage(ImageView image, int index){
        String fileName = gameTypeManager.determineThemeSelected() + (index);
        int selectedImageId = getResources().getIdentifier(fileName, getString(R.string.drawable), AchievementCelebrationActivity.this.getPackageName());
        Bitmap originalBitMap = BitmapFactory.decodeResource(getResources(), selectedImageId);
        Resources resource = getResources();
        image.setImageDrawable(new BitmapDrawable(resource, originalBitMap));
    }


    private void changeTheme() {
        Button options = findViewById(R.id.btnAchieveOptions);
        options.setOnClickListener(view -> {
            Intent optionsActivity = SettingsActivity.makeLaunchIntent(AchievementCelebrationActivity.this);
            startActivity(optionsActivity);
        });
    }

    private void setAchievementIcon() {
        image = findViewById(R.id.ibAchievementIcon);
        String fileName = gameTypeManager.determineThemeSelected() + (levelIndex + 1);
        int selectedImageId = getResources().getIdentifier(fileName, getString(R.string.drawable), AchievementCelebrationActivity.this.getPackageName());
        Bitmap originalBitMap = BitmapFactory.decodeResource(getResources(), selectedImageId);
        Resources resource = getResources();
        image.setImageDrawable(new BitmapDrawable(resource, originalBitMap));
    }

    private void congrats() {
        RotateAnimation anim = new RotateAnimation(0f, 360f, 250f, 260f);
        anim.setRepeatCount(Animation.REVERSE);
        anim.setDuration(900);
        image.startAnimation(anim);

        soundPool.play(audioSound, 1, 1, 0, 0, 1);
    }

    private void repeatAnimSound(){
        image.setOnClickListener(view -> congrats());
    }

    private void retrieveStoredDataIntent() {
        Intent prevIntent = getIntent();
        Bundle b = prevIntent.getExtras();

        if (b != null) {
            GameType gameType = gameTypeManager.get(rootPos);

            if (pos == -1){
                game = gameType.getLatestGame();
            } else {
                game = gameType.get(pos);
            }
        }

        numPlayers = game.getNumPlayer();
        sumScore = game.getSumScores();
    }

    private void displayNextAchievementName() {
        TextView displayNextAchieve = findViewById(R.id.tvNextAchieveName);

        int themeIndex = gameTypeManager.getThemeIndex();

        if (themeIndex != 0){
            themeIndex--;
        }

        if (levelIndex > 0){
            levelIndex--;
            String nextAchievementScore = achievementNames[themeIndex][levelIndex];
            displayNextAchieve.setText(getString(R.string.next_achievement) + nextAchievementScore);
        } else {
            displayNextAchieve.setVisibility(View.INVISIBLE);
        }
    }
}
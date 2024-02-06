package ca.cmpt276.coopachievement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.cmpt276.coopachievement.model.AchievementLevels;
import ca.cmpt276.coopachievement.model.AchievementManager;
import ca.cmpt276.coopachievement.model.Game;
import ca.cmpt276.coopachievement.model.GameType;
import ca.cmpt276.coopachievement.model.GameTypeManager;

/**
 * Displays achievement levels for each game type displaying min score needed to achieve
 * each level
 */

public class AchievementLevelListActivity extends AppCompatActivity {

    public static final int NORMAL_INDEX = 1;
    private final List<AchievementManager> achievementLevels = new ArrayList<>();
    private GameType gameType;
    private final GameTypeManager gameTypeManager = GameTypeManager.getInstance();
    private int numPlayers = HomeActivity.DEFAULT_NUM_PLAYERS;
    private ArrayAdapter<AchievementManager> adapter;
    private ListView list;
    private EditText etAchievementNumPlayer;
    private Spinner spinner;
    private String difficulty;
    Game game;
    ArrayAdapter<CharSequence> spinnerAdapter;

    public static Intent makeLaunchIntent(Context c, int posGTM) {
        Intent intent = new Intent(c, AchievementLevelListActivity.class);
        intent.putExtra(HomeActivity.POSITION_GTM, posGTM);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_list);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(R.string.achievement_level_title);
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        etAchievementNumPlayer = findViewById(R.id.etAchievementNumPlayer);
        etAchievementNumPlayer.addTextChangedListener(achievementTextWatcher);
        spinner = findViewById(R.id.spinner_dropdown_achievement);

        retrieveStoredDataIntent();

        difficulty = "Normal";
        int poorScore = gameType.getExpectedPoorScore();
        int greatScore = gameType.getExpectedGreatScore();
        game = new Game(numPlayers, 0, null, poorScore, greatScore, difficulty);

        populateAchievementList(1);
        populateAchievementListView();
        setUpSpinner();
    }

    private void retrieveStoredDataIntent() {
        Intent prevIntent = getIntent();
        Bundle b = prevIntent.getExtras();
        int rootPos = (int) b.get(HomeActivity.POSITION_GTM);
        gameType = gameTypeManager.get(rootPos);
    }

    private final TextWatcher achievementTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String achievementNumPlayer = etAchievementNumPlayer.getText().toString();
            if (!achievementNumPlayer.equals("0") && !achievementNumPlayer.isEmpty()) {
                numPlayers = Integer.parseInt(achievementNumPlayer);
                populateAchievementList(numPlayers);
            }
        }
        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void populateAchievementList(int numPlayers) {
        achievementLevels.clear();

        achievementLevels.add(new AchievementManager(AchievementLevels.ACHIEVEMENT_LEVEL8.getValue(), game.tierIncrement(8)*numPlayers,1));
        achievementLevels.add(new AchievementManager(AchievementLevels.ACHIEVEMENT_LEVEL7.getValue(), game.tierIncrement(7)*numPlayers,2));
        achievementLevels.add(new AchievementManager(AchievementLevels.ACHIEVEMENT_LEVEL6.getValue(), game.tierIncrement(6)*numPlayers,3));
        achievementLevels.add(new AchievementManager(AchievementLevels.ACHIEVEMENT_LEVEL5.getValue(), game.tierIncrement(5)*numPlayers,4));
        achievementLevels.add(new AchievementManager(AchievementLevels.ACHIEVEMENT_LEVEL4.getValue(), game.tierIncrement(4)*numPlayers,5));
        achievementLevels.add(new AchievementManager(AchievementLevels.ACHIEVEMENT_LEVEL3.getValue(), game.tierIncrement(3)*numPlayers,6));
        achievementLevels.add(new AchievementManager(AchievementLevels.ACHIEVEMENT_LEVEL2.getValue(), game.tierIncrement(2)*numPlayers,7));
        achievementLevels.add(new AchievementManager(AchievementLevels.ACHIEVEMENT_LEVEL1.getValue(), game.tierIncrement(1)*numPlayers,8));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            list.setVisibility(View.INVISIBLE);
            list.setVisibility(View.VISIBLE);
        }
    }

    private void populateAchievementListView() {
        adapter = new MyListAdapter();
        list = findViewById(R.id.listViewAchievement);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<AchievementManager>{
        public MyListAdapter(){
            super(AchievementLevelListActivity.this, R.layout.clv_achievement_list, achievementLevels);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;

            if (itemView == null){
                itemView = getLayoutInflater().inflate(R.layout.clv_achievement_list, parent, false);
            }

            AchievementManager currentAchievement = achievementLevels.get(position);

            ImageView imageView = itemView.findViewById(R.id.item_achievement_lvl_icon);
            String fileName = gameTypeManager.determineThemeSelected() + currentAchievement.getIconID();
            int selectedImageId = getResources().getIdentifier(fileName, getString(R.string.drawable), AchievementLevelListActivity.this.getPackageName());
            Bitmap originalBitMap = BitmapFactory.decodeResource(getResources(), selectedImageId);
            Resources resource = getResources();
            imageView.setImageDrawable(new BitmapDrawable(resource, originalBitMap));

            TextView level = itemView.findViewById(R.id.item_txt_achievement_lvl);
            level.setText(currentAchievement.getLevel());

            TextView rankCount = itemView.findViewById(R.id.tvRankObtainedCount);
            rankCount.setText(getString(R.string.rank_count, gameType.getRanksEarned()[position]));

            TextView minScore = itemView.findViewById(R.id.item_txt_achievement_min_score);
            minScore.setText(getString(R.string.min_score, currentAchievement.getMinScore()));

            return itemView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(R.anim.no_animation,R.anim.slide_in_bottom);
            return true;
        }
        return false;
    }

    public void setUpSpinner(){
        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.difficulty, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(NORMAL_INDEX);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                difficulty = parent.getItemAtPosition(position).toString();
                game.setDifficulty(difficulty);
                populateAchievementList(numPlayers);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Stub for now? do we want to add something?
            }
        });
    }
}
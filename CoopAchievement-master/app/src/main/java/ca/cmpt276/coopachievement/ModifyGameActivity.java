package ca.cmpt276.coopachievement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.coopachievement.model.AchievementLevels;
import ca.cmpt276.coopachievement.model.AchievementManager;
import ca.cmpt276.coopachievement.model.Game;
import ca.cmpt276.coopachievement.model.GameType;
import ca.cmpt276.coopachievement.model.GameTypeManager;

/**
 * Activity to modify an existing game or add a new game within an existing game type
 */

public class ModifyGameActivity extends AppCompatActivity{

    private final List<AchievementManager> achievementLevels = new ArrayList<>();
    private final GameTypeManager gameTypeManager = GameTypeManager.getInstance();
    private GameType gameType;
    private Game game;
    private AchievementLevels currAchievement;
    private int pos;
    private int rootPos;
    private int prevNumPlayer;
    private final ArrayList<Integer> prevPlayerScores = new ArrayList<>();
    private EditText numPlayersInput;
    private Button btnPlayers;
    private TextView achievementTier;
    private TextView tvSumScore;
    private ArrayAdapter<AchievementManager> adapter;
    private ListView list;
    private Spinner spinner;
    private SoundPool soundPool;
    private int audioSound;
    private String difficulty;
    private int numPlayers = HomeActivity.DEFAULT_NUM_PLAYERS;
    private int sumScore;
    private int prevSumScore;
    private Bitmap prevGamePhoto;

    public static Intent makeLaunchIntent(Context c, int posGTM, int posGT) {
        Intent intent = new Intent(c, ModifyGameActivity.class);
        intent.putExtra(HomeActivity.POSITION_GTM, posGTM);
        intent.putExtra(HomeActivity.POSITION_GT, posGT);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_game);

        numPlayersInput = findViewById(R.id.etNumPlayers);
        btnPlayers = findViewById(R.id.btnPlayers);
        tvSumScore = findViewById(R.id.tvSumScore);
        achievementTier = findViewById(R.id.tvAchievementLevelResult);
        spinner = findViewById(R.id.spinner_dropdown);
        btnPlayers.setEnabled(false);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();
        audioSound = soundPool.load(this, R.raw.clap, 1);

        retrieveStoredDataIntent();
        setUpActionBar();
        populateAchievementList(prevNumPlayer);
        populateListView();
        setUpSpinner();
    }

    @Override
    protected void onStart() {
        super.onStart();
        sumScore = game.calculateSumScore();
        tvSumScore.setText(getString(R.string.total_score, sumScore));
        updateAchievementReached();
    }

    private void retrieveStoredDataIntent() {
        Intent prevIntent = getIntent();
        Bundle b = prevIntent.getExtras();

        if (b != null) {
            rootPos = (int) b.get(HomeActivity.POSITION_GTM);
            pos = (int) b.get(HomeActivity.POSITION_GT);
            gameType = gameTypeManager.get(rootPos);
            int poorScore = gameType.getExpectedPoorScore();
            int greatScore = gameType.getExpectedGreatScore();

            if (pos == HomeActivity.NEW_GAME_POS) {
                game = new Game(prevNumPlayer, 0, null, poorScore, greatScore, difficulty);
                gameType.addGame(game);
                prevNumPlayer = HomeActivity.DEFAULT_NUM_PLAYERS;
                initializeText();
            } else {
                initializeText();
                game = gameType.get(pos);
                game.setExpectedPoorScore(poorScore);
                game.setExpectedGreatScore(greatScore);
                prevNumPlayer = game.getNumPlayer();
                prevSumScore = game.getSumScores();
                prevGamePhoto = game.getGamePhoto();

                if (game.getPlayerScores() != null) {
                    prevPlayerScores.addAll(game.getPlayerScores());
                }
                populateInputFields();
            }
        }
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        if (pos == HomeActivity.NEW_GAME_POS) {
            actionBar.setTitle(R.string.add_new_game_title);
        } else {
            actionBar.setTitle(R.string.edit_game_title);
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initializeText() {
        numPlayersInput.addTextChangedListener(achievementTextWatcher);
    }

    private void populateInputFields() {
        numPlayers = game.getNumPlayer();
        sumScore = game.calculateSumScore();
        numPlayersInput.setText(String.valueOf(numPlayers));
    }

    private void populateAchievementList(int numPlayers) {
        achievementLevels.clear();

        achievementLevels.add(new AchievementManager(AchievementLevels.ACHIEVEMENT_LEVEL8.getValue(), game.tierIncrement(8)*numPlayers, 1));
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

    private void populateListView() {
        adapter = new MyListAdapter();
        list = findViewById(R.id.listView_achievement_levels);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<AchievementManager> {
        public MyListAdapter() {
            super(ModifyGameActivity.this, R.layout.clv_achievement_list, achievementLevels);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.clv_achievement_list, parent, false);
            }

            AchievementManager currentAchievement = achievementLevels.get(position);

            ImageView imageView = itemView.findViewById(R.id.item_achievement_lvl_icon);
            String fileName = gameTypeManager.determineThemeSelected() + currentAchievement.getIconID();
            int selectedImageId = getResources().getIdentifier(fileName, getString(R.string.drawable), ModifyGameActivity.this.getPackageName());
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
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();

        if (pos == HomeActivity.NEW_GAME_POS) {
            inflater.inflate(R.menu.menu_add_game, menu);
        } else {
            inflater.inflate(R.menu.menu_edit_game, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setTitle(R.string.warning_delete_title)
                    .setMessage(R.string.warning_delete_message)
                    .setPositiveButton(R.string.yes_option, (dialog, which) -> {
                        gameType.setRanksEarned(Integer.parseInt(game.getAchievementLevel().getLevel()),
                                gameType.getRanksEarned()[Integer.parseInt(game.getAchievementLevel().getLevel())]-1);
                        gameType.deleteGame(pos);
                        Toast.makeText(this, R.string.deleting, Toast.LENGTH_SHORT).show();
                        saveData();
                        finish();
                    })
                    .setNegativeButton(R.string.no_option, null)
                    .setCancelable(false)
                    .show();
        }

        if (item.getItemId() == R.id.action_save) {
            if (!userInputValid()) {
                Toast.makeText(this, R.string.input_validation, Toast.LENGTH_SHORT).show();
            } else if (game.getEncodedPhoto() == null) {
                Toast.makeText(this, "Please take a game photo", Toast.LENGTH_SHORT).show();
            } else {
                numPlayers = Integer.parseInt(numPlayersInput.getText().toString());
                soundPool.play(audioSound, 1, 1, 0, 0, 1);
                if (pos == HomeActivity.NEW_GAME_POS) {
                    addNewGame(numPlayers, sumScore);
                } else {
                    editGame(numPlayers, sumScore);
                }
                saveData();
                startAchieveCelebActivity();
            }
        }

        // RETURN BUTTON
        else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void startAchieveCelebActivity() {
        String achievementScore = currAchievement.getLevel();
        Intent achievementCelebration = AchievementCelebrationActivity.makeLaunchIntent(ModifyGameActivity.this, achievementScore, rootPos, pos);
        startActivity(achievementCelebration);
        finish();
    }

    private void addNewGame(int numPlayers, int sumScore) {
        game = gameType.get(gameType.size()-1);
        game.setNumPlayer(numPlayers);
        game.setSumScores(sumScore);
        game.setDifficulty(difficulty);
        game.setAchievementLevel(currAchievement);
        gameType.setRanksEarned(Integer.parseInt(currAchievement.getLevel()),
                gameType.getRanksEarned()[Integer.parseInt(currAchievement.getLevel())]+1);
    }

    private void editGame(int numPlayers, int sumScore) {
        game.setNumPlayer(numPlayers);
        game.setSumScores(sumScore);
        gameType.setRanksEarned(Integer.parseInt(game.getAchievementLevel().getLevel()),
                gameType.getRanksEarned()[Integer.parseInt(game.getAchievementLevel().getLevel())]-1);
        gameType.setRanksEarned(Integer.parseInt(currAchievement.getLevel()),
                gameType.getRanksEarned()[Integer.parseInt(currAchievement.getLevel())]+1);
        game.setAchievementLevel(currAchievement);
        game.setDifficulty(difficulty);
    }

    @Override
    public void onBackPressed() {
        boolean notSamePlayer = !numPlayersInput.getText().toString().equals(""+prevNumPlayer);
        boolean notSameScore = !(sumScore == prevSumScore);

        if (notSamePlayer || notSameScore) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setTitle(R.string.warning_undo_title)
                    .setMessage(R.string.warning_undo_message)
                    .setPositiveButton(R.string.yes_option, (dialog, which) -> {
                        finish();

                        if (pos == HomeActivity.NEW_GAME_POS) {
                            gameType.deleteGame(gameType.size()-1);
                            overridePendingTransition(R.anim.no_animation,R.anim.slide_in_bottom);
                        } else {
                            game.setPlayerScores(prevPlayerScores);
                            game.setNumPlayer(prevNumPlayer);
                            game.setSumScores(prevSumScore);
                            game.setGamePhoto(prevGamePhoto);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        }
                        Toast.makeText(this, R.string.alert_dialog_returning, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(R.string.no_option, null)
                    .show();
        } else {
            finish();

            if (pos == HomeActivity.NEW_GAME_POS) {
                gameType.deleteGame(gameType.size()-1);
                overridePendingTransition(R.anim.no_animation,R.anim.slide_in_bottom);
            } else {
                game.setPlayerScores(prevPlayerScores);
                game.setNumPlayer(prevNumPlayer);
                game.setSumScores(prevSumScore);
                game.setGamePhoto(prevGamePhoto);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
            Toast.makeText(this, R.string.alert_dialog_returning, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean userInputValid() {
        String numPlayersString = numPlayersInput.getText().toString();
        return (!numPlayersString.isEmpty() && !numPlayersString.equals(getString(R.string.zero)));
    }

    private final TextWatcher achievementTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            updateAchievementReached();
            tvSumScore.setText(R.string.empty_dash);
            achievementTier.setText(R.string.reinput_player_score);
        }
        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private void updateAchievementReached() {

        if (userInputValid()) {
            numPlayers = Integer.parseInt(numPlayersInput.getText().toString());
            populateAchievementList(numPlayers);

            if (numPlayers != 0) {
                btnPlayers.setOnClickListener(view -> {
                    Intent i = PlayerActivity.makeLaunchIntent(ModifyGameActivity.this, rootPos, pos, numPlayers);

                    startActivity(i);
                    overridePendingTransition(R.anim.slide_out_bottom,R.anim.no_animation);
                });
                btnPlayers.setEnabled(true);
            }
            game.setNumPlayer(numPlayers);
            game.setSumScores(sumScore);
            currAchievement = game.calculateTier();
            achievementTier.setText(currAchievement.getValue());
        } else {
            achievementTier.setText(R.string.empty_dash);
            btnPlayers.setEnabled(false);
        }
    }

    public void saveData() {
        SharedPreferences sPrefs = getSharedPreferences(HomeActivity.GAME_TYPE_MANAGER, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(gameTypeManager);
        prefsEditor.putString(HomeActivity.GAME_TYPE_MANAGER, json);
        prefsEditor.apply();
    }

    private void setUpSpinner(){
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.difficulty, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        if (pos != HomeActivity.NEW_GAME_POS) {
            for (int dropDownIndex = 0 ; dropDownIndex <= 2; dropDownIndex++){

                if (spinner.getItemAtPosition(dropDownIndex).equals(game.getDifficulty())){
                    spinner.setSelection(dropDownIndex);
                }
            }
        } else {
            spinner.setSelection(1);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                difficulty = parent.getItemAtPosition(position).toString();
                game.setDifficulty(difficulty);
                updateAchievementReached();
                populateAchievementList(numPlayers);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
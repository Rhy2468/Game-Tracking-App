package ca.cmpt276.coopachievement;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

import ca.cmpt276.coopachievement.adapter.GameAdapter;
import ca.cmpt276.coopachievement.model.AchievementLevels;
import ca.cmpt276.coopachievement.model.Game;
import ca.cmpt276.coopachievement.model.GameType;
import ca.cmpt276.coopachievement.model.GameTypeManager;

/**
 * Manages games within a game type, Allows user to navigate to add game activity and view
 * achievement levels activity. Also allows user to edit game type data
 */

public class GameTypeActivity extends AppCompatActivity implements GameAdapter.OnGameListener {

    private final GameTypeManager gameTypeManager = GameTypeManager.getInstance();
    private GameType gameType;
    private final ArrayList<Game> gameArray = new ArrayList<>();
    private GameAdapter adapter;
    private int rootPos;
    private boolean inEditMode = false;
    private int clickedPos;
    private EditText etExpectedGreatScore;
    private EditText etExpectedPoorScore;
    private EditText etGameTitle;
    private View divExpectedGreatRed;
    private View divExpectedPoorRed;
    private View divGameTitleRed;
    private Button editButton;
    private Button saveButton;
    private ImageButton changeIconButton;
    private ImageButton customPhotoButton;
    private ImageView gameIcon;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    public static final int NUM_ICONS = 10;

    public static Intent makeLaunchIntent(Context c, int pos) {
        Intent intent = new Intent(c, GameTypeActivity.class);
        intent.putExtra(HomeActivity.POSITION_GTM, pos);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        etExpectedGreatScore = findViewById(R.id.etEditGameConfigGreatScore);
        etExpectedPoorScore = findViewById(R.id.etEditGameConfigPoorScore);
        etGameTitle = findViewById(R.id.inputGameTypeTitle);

        divExpectedGreatRed = findViewById(R.id.divExpectedGreatRed);
        divExpectedPoorRed = findViewById(R.id.divExpectedPoorRed);
        divGameTitleRed = findViewById(R.id.divGameTitleRed);

        editButton = findViewById(R.id.btnEditGameConfigEdit);
        saveButton = findViewById(R.id.btnEditGameConfigSave);

        Intent prevIntent = getIntent();
        Bundle b = prevIntent.getExtras();
        if (b != null) {
            rootPos = (int) b.get(HomeActivity.POSITION_GTM);
            gameType = gameTypeManager.get(rootPos);
            initializeList();
        }
        repopulateScoreFields();

        Button btnAddNewGame = findViewById(R.id.btnAddNewGame);
        btnAddNewGame.setOnClickListener(view -> {
            if (!inEditMode) {
                Intent i = ModifyGameActivity.makeLaunchIntent(GameTypeActivity.this, rootPos, HomeActivity.NEW_GAME_POS);
                startActivity(i);
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.no_animation);
            } else {
                Toast.makeText(GameTypeActivity.this, R.string.change_save, Toast.LENGTH_SHORT).show();
            }
        });

        Button btnViewLevel = findViewById(R.id.btnViewLevel);
        btnViewLevel.setOnClickListener(view -> {
            if (!inEditMode) {
                Intent intent = AchievementLevelListActivity.makeLaunchIntent(GameTypeActivity.this, rootPos);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_out_bottom, R.anim.no_animation);
            } else {
                Toast.makeText(GameTypeActivity.this, R.string.change_save, Toast.LENGTH_SHORT).show();
            }
        });

        setupEditMode();
        setupCamera();
    }

    private void setupEditMode() {
        saveButton.setVisibility(View.INVISIBLE);
        saveButton.setEnabled(false);
        changeIconButton = findViewById(R.id.ibChangeIcon);
        customPhotoButton = findViewById(R.id.ibCustomPhotoIcon);
        changeIconButton.setVisibility(View.INVISIBLE);
        customPhotoButton.setVisibility(View.INVISIBLE);
        editButton.setOnClickListener(v -> {
            inEditMode = true;
            switchEditSaveButtons(editButton,saveButton);
            changeIconButton.setVisibility(View.VISIBLE);
            customPhotoButton.setVisibility(View.VISIBLE);
            editGameTypeInfo();
            Toast.makeText(this, R.string.editing, Toast.LENGTH_SHORT).show();
        });
    }

    private void editGameTypeInfo() {
        makeInputsEditable();

        changeIconButton.setOnClickListener(v ->{
            int imageIndex = changeGameTypeIcon();
            gameType.setImageSelectedIndex(imageIndex);
        });

        saveButton.setOnClickListener(v -> {
            String expectedGreatScore = etExpectedGreatScore.getText().toString();
            String expectedPoorScore = etExpectedPoorScore.getText().toString();
            String gameTitle = etGameTitle.getText().toString();

            boolean checkEmptyInputs = !expectedPoorScore.isEmpty() && !expectedGreatScore.isEmpty() && !gameTitle.isEmpty();
            if (checkEmptyInputs){
                int expectedHighScore = Integer.parseInt(expectedGreatScore);
                int expectedLowScore = Integer.parseInt(expectedPoorScore);

                boolean checkGreatBiggerThan = (expectedHighScore >= expectedLowScore + AchievementLevels.NUM_LEVELS);
                if (checkGreatBiggerThan) {
                    gameType.setExpectedGreatScore(expectedHighScore);
                    gameType.setExpectedPoorScore(expectedLowScore);
                    gameType.setName(gameTitle);
                    switchEditSaveButtons(saveButton,editButton);

                    etExpectedGreatScore.setFocusable(false);
                    etExpectedPoorScore.setFocusable(false);
                    etGameTitle.setFocusable(false);

                    divGameTitleRed.setVisibility(View.INVISIBLE);
                    divExpectedGreatRed.setVisibility(View.INVISIBLE);
                    divExpectedPoorRed.setVisibility(View.INVISIBLE);
                    inEditMode = false;
                    recalculateAchievement();
                    saveData();
                    changeIconButton.setVisibility(View.INVISIBLE);
                    customPhotoButton.setVisibility(View.INVISIBLE);
                    Toast.makeText(this, R.string.saving, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GameTypeActivity.this, R.string.restriction_invalid_score_values, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.incomplete_field, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCamera() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                assert data != null;
                Bundle bundle = data.getExtras();
                Bitmap photo = (Bitmap) bundle.get("data");
                gameIcon.setImageBitmap(photo);
                gameType.setImageSelectedIndex(-1);
                gameType.setPhotoTaken(photo);
            }
        });

        customPhotoButton.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(cameraIntent);
        });
    }

    private void makeInputsEditable() {
        etExpectedGreatScore.setFocusable(true);
        etExpectedGreatScore.setFocusableInTouchMode(true);
        etExpectedGreatScore.setClickable(true);
        etExpectedPoorScore.setFocusable(true);
        etExpectedPoorScore.setFocusableInTouchMode(true);
        etExpectedPoorScore.setClickable(true);
        etGameTitle.setFocusable(true);
        etGameTitle.setFocusableInTouchMode(true);
        etGameTitle.setClickable(true);
        divGameTitleRed.setVisibility(View.VISIBLE);
        divExpectedGreatRed.setVisibility(View.VISIBLE);
        divExpectedPoorRed.setVisibility(View.VISIBLE);
    }

    private int changeGameTypeIcon() {
        int currImageIndex = gameType.getImageSelectedIndex();
        currImageIndex++;
        currImageIndex = currImageIndex % NUM_ICONS;

        if (currImageIndex == 0){
            currImageIndex++;
        }

        String fileName = getString(R.string.icon_prefix) + currImageIndex;
        int selectedImageId = getResources().getIdentifier(fileName, getString(R.string.drawable), GameTypeActivity.this.getPackageName());
        Bitmap originalBitMap = BitmapFactory.decodeResource(getResources(), selectedImageId);
        Resources resource = getResources();
        gameIcon.setImageDrawable(new BitmapDrawable(resource, originalBitMap));

        return currImageIndex;
    }

    private void switchEditSaveButtons(Button btn1, Button btn2){
        btn1.setEnabled(false);
        btn1.setVisibility(View.INVISIBLE);
        btn2.setVisibility(View.VISIBLE);
        btn2.setEnabled(true);
    }

    private void repopulateScoreFields() {
        etExpectedGreatScore.setText(String.valueOf(gameType.getExpectedGreatScore()));
        etExpectedPoorScore.setText(String.valueOf(gameType.getExpectedPoorScore()));
        etGameTitle.setText(gameType.getName());
        etGameTitle.setFocusable(false);
        etExpectedGreatScore.setFocusable(false);
        etExpectedPoorScore.setFocusable(false);

        gameIcon = findViewById(R.id.ivGameTypeActIcon);
        int imageIndex = gameType.getImageSelectedIndex();
        if (imageIndex == -1) {
            Bitmap photoTaken = gameType.getPhotoTaken();
            gameIcon.setImageBitmap(photoTaken);
        } else {
            String fileName = getString(R.string.icon_prefix) + imageIndex;
            int selectedImageId = getResources().getIdentifier(fileName, getString(R.string.drawable), GameTypeActivity.this.getPackageName());
            Bitmap originalBitMap = BitmapFactory.decodeResource(getResources(), selectedImageId);
            Resources resource = getResources();
            gameIcon.setImageDrawable(new BitmapDrawable(resource, originalBitMap));
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onStart() {
        super.onStart();
        while (true) {
            if (populateGameList() == 0) break;
        }
        if (clickedPos > HomeActivity.NEW_GAME_POS) {
            adapter.notifyDataSetChanged();
        }

        TextView emptyHelp = findViewById(R.id.tvEmptyGameArrayText);
        ImageView arrow = findViewById(R.id.ivEmptyGameArrayArrow);

        if (gameArray.isEmpty()) {
            emptyHelp.setVisibility(View.VISIBLE);
            arrow.setVisibility(View.VISIBLE);
        } else {
            emptyHelp.setVisibility(View.GONE);
            arrow.setVisibility(View.GONE);
        }
    }

    private void initializeList() {
        RecyclerView rvDisplayGames = findViewById(R.id.rvDisplayGames);
        adapter = new GameAdapter(this, this, gameArray);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvDisplayGames.setLayoutManager(linearLayoutManager);
        rvDisplayGames.setItemAnimator(null);
        rvDisplayGames.setAdapter(adapter);
    }

    private int populateGameList() {
        if (gameArray.size() < gameType.size()) {
            Game currentGame = gameType.get(gameArray.size());
            gameArray.add(currentGame);
            adapter.notifyItemChanged(gameArray.size()-1);
            adapter.notifyItemRangeChanged(gameArray.size()-1, gameArray.size());
            return 1;
        } else if (gameArray.size() > gameType.size()) {
            gameArray.remove(clickedPos);
            adapter.notifyItemChanged(clickedPos);
            adapter.notifyItemRangeChanged(gameArray.size()-1, gameArray.size());
            return 1;
        } else {
            return 0;
        }
    }

    private void recalculateAchievement() {
        for (int i = 0; i < gameType.size(); i++) {
            Game currentGame = gameType.get(i);
            AchievementLevels currAchievement = currentGame.calculateTier();
            currentGame.setAchievementLevel(currAchievement);
            adapter.notifyItemChanged(i);
        }
    }

    @Override
    public void onGameClick(int position) {
        clickedPos = position;
        Intent i = ModifyGameActivity.makeLaunchIntent(GameTypeActivity.this, rootPos, position);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!inEditMode) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            Toast.makeText(GameTypeActivity.this, R.string.change_save, Toast.LENGTH_SHORT).show();
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
}
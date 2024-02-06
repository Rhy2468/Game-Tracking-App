package ca.cmpt276.coopachievement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.Objects;

import ca.cmpt276.coopachievement.model.AchievementLevels;
import ca.cmpt276.coopachievement.model.GameType;
import ca.cmpt276.coopachievement.model.GameTypeManager;

/**
 * Handles UI and basic adding game config operations including making an button matrix for
 * icons, grabbing user input, and injecting data into game config manager (GameTypeManager)
 */

public class AddGameTypeActivity extends AppCompatActivity {
    private static final int NUM_ROWS = 3;
    private static final int NUM_COLS = 3;
    private int imageIndexCounter = 1;
    private boolean defaultIconSelected;
    private final GameTypeManager gameTypeManager = GameTypeManager.getInstance();
    private final GameType newGameType = new GameType();
    private EditText gameNameInput;
    private EditText expectedLowScoreInput;
    private EditText expectedHighScoreInput;
    private ImageView selectedImage;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, AddGameTypeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        defaultIconSelected = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game_type);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        gameNameInput = findViewById(R.id.etAddGameTypeGameName);
        expectedLowScoreInput = findViewById(R.id.etAddGameTypeExpectedLow);
        expectedHighScoreInput = findViewById(R.id.etAddGameTypeExpectedHigh);
        selectedImage = findViewById(R.id.ivAddGameTypeSelectedImage);
        selectedImage.setTag(getString(R.string.default_tag));

        populateButtons();
        setupCamera();
    }

    private void setupCamera() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                assert data != null;
                Bundle bundle = data.getExtras();
                Bitmap photo = (Bitmap) bundle.get("data");
                selectedImage.setImageBitmap(photo);
                newGameType.setImageSelectedIndex(-1);
                defaultIconSelected = false;
                newGameType.setPhotoTaken(photo);
            }
        });

        Button btnOpenCamera = findViewById(R.id.btn_add_game_type_camera);
        btnOpenCamera.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(cameraIntent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_game_type, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            if (checkGameNameInput()) {
                if (defaultIconSelected) {
                    newGameType.setImageSelectedIndex(1);
                }
                gameTypeManager.addGame(newGameType);
                saveData();
                finish();
            }
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(R.anim.no_animation,R.anim.slide_in_bottom);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        boolean checkIfInputsFilled = (!gameNameInput.getText().toString().equals(getString(R.string.empty_string))
                || !expectedLowScoreInput.getText().toString().equals(getString(R.string.empty_string))
                || !expectedHighScoreInput.getText().toString().equals(getString(R.string.empty_string))
                || (selectedImage.getTag().equals(getString(R.string.custom_tag))));
        if (checkIfInputsFilled) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setTitle(R.string.warning_undo_title)
                    .setMessage(R.string.warning_undo_message)
                    .setPositiveButton(R.string.alert_dialog_yes, (dialog, which) -> {
                        finish();
                        overridePendingTransition(R.anim.no_animation,R.anim.slide_in_bottom);
                        Toast.makeText(this, R.string.alert_dialog_returning, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(R.string.alert_dialog_no, null)
                    .show();
        } else {
            finish();
            overridePendingTransition(R.anim.no_animation,R.anim.slide_in_bottom);
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

    private boolean checkGameNameInput() {
        String gameName = gameNameInput.getText().toString();
        String expectedLowScoreString = expectedLowScoreInput.getText().toString();
        String expectedHighScoreString = expectedHighScoreInput.getText().toString();

        boolean checkEmptyInputs = !gameName.isEmpty() &&
                !expectedLowScoreString.isEmpty() &&
                !expectedHighScoreString.isEmpty();
        if (checkEmptyInputs){
            int expectedLowScore = Integer.parseInt(expectedLowScoreString);
            int expectedHighScore = Integer.parseInt(expectedHighScoreString);

            boolean checkGreatBiggerThan = (expectedHighScore >= expectedLowScore + AchievementLevels.NUM_LEVELS);
            if (checkGreatBiggerThan) {
                newGameType.setName(gameName);
                newGameType.setExpectedPoorScore(expectedLowScore);
                newGameType.setExpectedGreatScore(expectedHighScore);
                return true;
            } else {
                Toast.makeText(AddGameTypeActivity.this, R.string.restriction_invalid_score_values, Toast.LENGTH_SHORT).show();
                return false;
            }

        } else {
            Toast.makeText(AddGameTypeActivity.this, R.string.restriction_invalid_score_inputs, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void populateButtons() {
        TableLayout tableLayout = findViewById(R.id.tlAddGameTypeIconMatrix);
        for (int row = 0; row < NUM_ROWS; row++){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT,
                    1.0f));
            tableLayout.addView(tableRow);

            for (int col = 0; col < NUM_COLS; col++){
                int FINAL_COL = col;
                int FINAL_ROW = row;
                Button button = new Button(this);
                button.setLayoutParams(new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        1.0f));
                button.setPadding(0, 0, 0, 0);
                setIconsOnButtons(button);
                button.setOnClickListener(v -> {
                    defaultIconSelected = false;
                    selectedImage.setTag(R.string.custom_tag);
                    presentCurrentIconPicked(FINAL_COL, FINAL_ROW);
                });
                tableRow.addView(button);
            }
        }
    }

    private void presentCurrentIconPicked(int col, int row) {
        int currSelectedIconIndex = row * NUM_COLS + col + 1;
        newGameType.setImageSelectedIndex(currSelectedIconIndex);

        String fileName = getString(R.string.icon_prefix) + currSelectedIconIndex;
        int selectedImageId = getResources().getIdentifier(fileName, getString(R.string.drawable), AddGameTypeActivity.this.getPackageName());
        Bitmap originalBitMap = BitmapFactory.decodeResource(getResources(), selectedImageId);
        Resources resource = getResources();
        selectedImage.setImageDrawable(new BitmapDrawable(resource, originalBitMap));
    }

    private void setIconsOnButtons(Button button) {
        String fileName = getString(R.string.icon_prefix) + imageIndexCounter;
        imageIndexCounter++;
        int imageId = getResources().getIdentifier(fileName, getString(R.string.drawable), AddGameTypeActivity.this.getPackageName());

        Bitmap originalBitMap = BitmapFactory.decodeResource(getResources(), imageId);
        Resources resource = getResources();
        button.setBackground(new BitmapDrawable(resource, originalBitMap));
    }
}
package ca.cmpt276.coopachievement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Objects;

import ca.cmpt276.coopachievement.adapter.GameTypeAdapter;
import ca.cmpt276.coopachievement.model.GameType;
import ca.cmpt276.coopachievement.model.GameTypeManager;

/**
 * Home screen activity that displays all/previous game types, allows user to navigate
 * to add game type activity, etc.
 */

public class HomeActivity extends AppCompatActivity implements GameTypeAdapter.OnGameTypeListener {

    public static final String GAME_TYPE_MANAGER = "GameTypeManager";
    public static final String POSITION_GTM = "posGTM";
    public static final String POSITION_GT = "posGT";
    public static final int DEFAULT_NUM_PLAYERS = 1;
    public static final int NEW_GAME_POS = -1;
    private GameTypeManager gameTypeManager;
    private final ArrayList<GameType> gameTypeArray = new ArrayList<>();
    private GameTypeAdapter adapter;
    private int clickedPos;
    private SharedPreferences sPrefs;
    private Gson gson;
    private String json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Objects.requireNonNull(getSupportActionBar()).hide();

        clickedPos = NEW_GAME_POS;
        setUpGameTypeManager();
        initializeList();
        Button btnAddNewGameType = findViewById(R.id.btnAddNewGameType);
        btnAddNewGameType.setOnClickListener(view -> {
            Intent i = AddGameTypeActivity.makeLaunchIntent(HomeActivity.this);
            startActivity(i);
            overridePendingTransition(R.anim.slide_out_bottom,R.anim.no_animation);
        });

        Button btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(view -> {
            Intent settings = SettingsActivity.makeLaunchIntent(HomeActivity.this);
            startActivity(settings);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        while (true) {
            if (populateGameTypeList() == 0) break;
        }
        if (clickedPos > NEW_GAME_POS) {
            adapter.notifyItemChanged(clickedPos);
        }
        TextView emptyHelp = findViewById(R.id.tvEmptyTypeArrayText);
        ImageView arrow = findViewById(R.id.ivEmptyTypeArrayArrow);
        if (gameTypeArray.isEmpty()) {
            emptyHelp.setVisibility(View.VISIBLE);
            arrow.setVisibility(View.VISIBLE);
        } else {
            emptyHelp.setVisibility(View.GONE);
            arrow.setVisibility(View.GONE);
        }
    }

    private int populateGameTypeList() {
        if (gameTypeArray.size() != gameTypeManager.size()) {
            GameType currentGameType = gameTypeManager.get(gameTypeArray.size());
            gameTypeArray.add(currentGameType);
            adapter.notifyItemChanged(gameTypeArray.size()-1);
            adapter.notifyItemRangeChanged(gameTypeArray.size()-1, gameTypeArray.size());
            return 1;
        } else {
            return 0;
        }
    }

    private void initializeList() {
        RecyclerView rvDisplayGameTypes = findViewById(R.id.rvDisplayGameTypes);
        adapter = new GameTypeAdapter(this, this, gameTypeArray);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvDisplayGameTypes.setLayoutManager(linearLayoutManager);
        rvDisplayGameTypes.setAdapter(adapter);
    }

    @Override
    public void onGameTypeClick(int position) {
        clickedPos = position;
        Intent i = GameTypeActivity.makeLaunchIntent(HomeActivity.this, position);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onGameTypeDeleteClick(int position) {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setTitle(R.string.warning_delete_title)
                .setMessage(R.string.warning_delete_message)
                .setPositiveButton(R.string.yes_option, (dialog, which) -> {
                    gameTypeArray.remove(position);
                    gameTypeManager.deleteGame(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, gameTypeArray.size());
                    onStart();
                    saveData();
                    Toast.makeText(this, R.string.deleting, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no_option, null)
                .setCancelable(false)
                .show();
    }

    public void saveData() {
        sPrefs = getSharedPreferences(GAME_TYPE_MANAGER, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sPrefs.edit();
        gson = new Gson();
        json = gson.toJson(gameTypeManager);
        prefsEditor.putString(GAME_TYPE_MANAGER, json);
        prefsEditor.apply();
    }

    private void setUpGameTypeManager() {
        sPrefs = getSharedPreferences(GAME_TYPE_MANAGER, MODE_PRIVATE);
        gson = new Gson();
        json = sPrefs.getString(GAME_TYPE_MANAGER, "");
        gameTypeManager = gson.fromJson(json, GameTypeManager.class);
        if (gameTypeManager == null) {
            gameTypeManager = GameTypeManager.getInstance();
        } else {
            GameTypeManager.setInstance(gameTypeManager);
        }
    }
}
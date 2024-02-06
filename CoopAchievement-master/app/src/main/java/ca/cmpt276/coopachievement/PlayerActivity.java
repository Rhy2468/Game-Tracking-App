package ca.cmpt276.coopachievement;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.cmpt276.coopachievement.adapter.PlayerAdapter;
import ca.cmpt276.coopachievement.model.Game;
import ca.cmpt276.coopachievement.model.GameType;
import ca.cmpt276.coopachievement.model.GameTypeManager;

/**
 * Activity to Handle player's score inputs
 */

public class PlayerActivity extends AppCompatActivity {

    private static final String NUMBER_PLAYERS = "NumberOfPlayer";
    private final GameTypeManager gameTypeManager = GameTypeManager.getInstance();
    private ArrayList<Integer> playerArray = new ArrayList<>();
    private Game game;
    private final ArrayList<Integer> prevPlayerScores = new ArrayList<>();
    private int numPlayers;
    private Button btnPlayerSave;
    private Button btnGamePhoto;
    private ImageView currentGamePhoto;
    private ActivityResultLauncher<Intent> activityResultLauncher;


    public static Intent makeLaunchIntent(Context c, int posGTM, int posGT, int numPlayers) {
        Intent intent = new Intent(c, PlayerActivity.class);
        intent.putExtra(NUMBER_PLAYERS, numPlayers);
        intent.putExtra(HomeActivity.POSITION_GT, posGT);
        intent.putExtra(HomeActivity.POSITION_GTM, posGTM);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        retrieveIntent();
        initializeList();
        btnPlayerSave = findViewById(R.id.btnPlayerSave);
        btnGamePhoto = findViewById(R.id.btn_take_game_photo);
        currentGamePhoto = findViewById(R.id.ivCurrentGamePhoto);

        setPreviousPlayerScores();
        setPreviousPhoto();

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.player_list);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setupCamera();
    }

    private void setPreviousPlayerScores() {
        if (playerArray != null) {
            prevPlayerScores.addAll(playerArray);
        }
    }

    private void setPreviousPhoto() {
        if (game.getEncodedPhoto() != null) {
            currentGamePhoto.setImageBitmap(game.getGamePhoto());
        }
    }

    private void setupCamera() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                assert data != null;
                Bundle bundle = data.getExtras();
                Bitmap photo = (Bitmap) bundle.get("data");
                currentGamePhoto.setImageBitmap(photo);
                game.setGamePhoto(photo);
            }
        });

        btnGamePhoto.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            activityResultLauncher.launch(cameraIntent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnPlayerSave.setOnClickListener(view -> {
            if (game.getEncodedPhoto() == null) {
                Toast.makeText(this, R.string.please_take_a_photo, Toast.LENGTH_SHORT).show();
            } else {
                game.setNumPlayer(numPlayers);
                finish();
                overridePendingTransition(R.anim.no_animation,R.anim.slide_in_bottom);
            }
        });
    }

    private void retrieveIntent() {
        Intent prevIntent = getIntent();
        Bundle b = prevIntent.getExtras();
        if (b != null) {
            int rootPos = (int) b.get(HomeActivity.POSITION_GTM);
            int pos = (int) b.get(HomeActivity.POSITION_GT);
            numPlayers = (int) b.get(NUMBER_PLAYERS);
            GameType gameType = gameTypeManager.get(rootPos);

            if (pos == HomeActivity.NEW_GAME_POS) {
                pos = gameType.size()-1;
            }

            game = gameType.get(pos);
            playerArray = game.getPlayerScores();
            int prevNumPlayer = playerArray.size();
            // handle exceptions when prev > new
            if (numPlayers > prevNumPlayer) {
                for (int i = 0; i < (numPlayers - prevNumPlayer); i++) {
                    if (game.getPrevPlayerScores().size() > i) {
                        playerArray.add(game.getPrevPlayerScores().get(i));
                    } else {
                        playerArray.add(0);
                    }
                }
            }
            for (int i = 0; i < numPlayers; i++) {
                if (game.getPlayerScores().get(i) != null) {
                    playerArray.set(i, game.getPlayerScores().get(i));
                }
            }
            if (numPlayers < prevNumPlayer) {
                game.getPrevPlayerScores().clear();
                for (int i = prevNumPlayer -1; i > numPlayers-1; i--) {
                    game.getPrevPlayerScores().add(playerArray.remove(i));
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // RETURN BUTTON
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void initializeList() {
        RecyclerView rvDisplayPlayers = findViewById(R.id.rvDisplayPlayers);
        PlayerAdapter adapter = new PlayerAdapter(this, playerArray);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvDisplayPlayers.setLayoutManager(linearLayoutManager);
        rvDisplayPlayers.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {

        if (!game.getPlayerScores().equals(prevPlayerScores)) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .setTitle(R.string.warning_undo_title)
                    .setMessage(R.string.warning_undo_message)
                    .setPositiveButton(R.string.yes_option, (dialog, which) -> {
                        finish();
                        game.setPlayerScores(prevPlayerScores);
                        overridePendingTransition(R.anim.no_animation,R.anim.slide_in_bottom);
                        Toast.makeText(this, R.string.alert_dialog_returning, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(R.string.no_option, null)
                    .show();
        } else {
            finish();
        }
    }
}
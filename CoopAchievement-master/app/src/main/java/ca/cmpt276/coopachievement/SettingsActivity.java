package ca.cmpt276.coopachievement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import ca.cmpt276.coopachievement.model.GameTypeManager;

/**
 * Activity to handle theme selection and change
 */

public class SettingsActivity extends AppCompatActivity {

    public static Intent makeLaunchIntent(Context c) {
        return new Intent(c, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.changeTheme);
        actionBar.setDisplayHomeAsUpEnabled(true);

        createRadioButtons();

        Button aboutPage = findViewById(R.id.btnAboutPage);
        aboutPage.setOnClickListener(view -> startActivity(AboutActivity.makeLaunchIntent(SettingsActivity.this)));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createRadioButtons(){
        RadioGroup group = findViewById(R.id.rgThemes);

        int[] theme = getResources().getIntArray(R.array.Themes);

        for (int themeIndex : theme) {
            RadioButton button = new RadioButton(this);

            if (themeIndex == 1) {
                button.setText(R.string.faces);
            } else if (themeIndex == 2) {
                button.setText(R.string.animals);
            } else if (themeIndex == 3) {
                button.setText(R.string.spongebob);
            }

            button.setOnClickListener(v -> {
                GameTypeManager.setThemeIndex(themeIndex);
                Toast.makeText(SettingsActivity.this, "Theme Changed!", Toast.LENGTH_SHORT).show();
            });
            group.addView(button);
        }
    }
}
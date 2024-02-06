package ca.cmpt276.coopachievement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ca.cmpt276.coopachievement.adapter.ResourcesAdapter;

/**
 * Activity to display resources used and credit developers
 */

public class AboutActivity extends AppCompatActivity {

    final ArrayList<ResourcesAdapter> items = new ArrayList<>();
    ListView list;

    public static Intent makeLaunchIntent(Context c) {
        return(new Intent(c, AboutActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        addResourceToArrayList();
        setUpListview();
        setUpListviewOnClick();
        setUpActionBar();
    }

    private void addResourceToArrayList() {
        items.add(new ResourcesAdapter(getString(R.string.achievement_faces), "https://manutessori.com/wp-content/uploads/2019/10/emotions.jpg"));
        items.add(new ResourcesAdapter(getString(R.string.achievement_animals), "https://cdn.pixabay.com/photo/2020/03/22/12/10/animal-cartoon-face-4956914_1280.png"));
        items.add(new ResourcesAdapter(getString(R.string.achievement_sb1), "https://www.polygon.com/tv/2019/6/4/18652440/spongebob-squarepants-prequel-series-spinoff-kamp-koral"));
        items.add(new ResourcesAdapter(getString(R.string.achievement_sb2), "https://www.columbiaspectator.com/spectrum/2015/01/29/do-i-drop-passfail-or-stick-class/"));
        items.add(new ResourcesAdapter(getString(R.string.achievement_sb3), "https://www.msureporter.com/2019/02/05/spongebob-got-his-moment-at-the-super-bowl/"));
        items.add(new ResourcesAdapter(getString(R.string.achievement_sb4), "https://www.pinterest.ca/pin/577727458432499283/"));
        items.add(new ResourcesAdapter(getString(R.string.achievement_sb5), "https://www.vox.com/culture/2018/2/14/17011344/valentines-day-gifs-forever-alone"));
        items.add(new ResourcesAdapter(getString(R.string.achievement_sb6), "https://sticker.ly/s/RGIB8H"));
        items.add(new ResourcesAdapter(getString(R.string.achievement_sb7), "https://www.facebook.com/1786644104898446/photos/a.1786647608231429/1786647614898095/?type=3"));
        items.add(new ResourcesAdapter(getString(R.string.achievement_sb8), "https://mashable.com/article/spongebob-squarepants-musical-broadway"));
        items.add(new ResourcesAdapter(getString(R.string.game_icons), "https://game-icons.net/"));
        items.add(new ResourcesAdapter(getString(R.string.wallpaper), "https://wallpapers.com/wallpapers/clouds-against-a-pastel-sky-background-ai8ivfi5ddx0qdyv.html"));
        items.add(new ResourcesAdapter(getString(R.string.gear_iron), "https://www.cleanpng.com/png-gear-icon-gears-png-file-114545/"));
        items.add(new ResourcesAdapter(getString(R.string.swap_icon), "https://icon-icons.com/icon/change/155682"));
        items.add(new ResourcesAdapter(getString(R.string.audio), "https://mixkit.co/free-sound-effects/clap/"));
    }

    private void setUpListview() {
        ArrayAdapter<ResourcesAdapter> adapter = new ArrayAdapter<>(this, R.layout.resource_listview, items);
        list = findViewById(R.id.itemListView);
        list.setAdapter(adapter);
    }

    private void setUpListviewOnClick() {
        list.setOnItemClickListener((parent, view, position, id) -> {
            Uri uri = Uri.parse(items.get(position).getLink());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.about);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return false;
    }
}
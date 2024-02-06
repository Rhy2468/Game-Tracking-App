package ca.cmpt276.coopachievement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.cmpt276.coopachievement.R;
import ca.cmpt276.coopachievement.model.AchievementLevels;
import ca.cmpt276.coopachievement.model.Game;
import ca.cmpt276.coopachievement.model.GameTypeManager;

/**
 * Handles game list logic within a game type
 */

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {
    private final Context context;
    private final OnGameListener gameListener;
    private final ArrayList<Game> gameArray;
    private final GameTypeManager gameTypeManager = GameTypeManager.getInstance();

    public GameAdapter(Context context, OnGameListener onGameListener, ArrayList<Game> gameArray) {
        this.context = context;
        this.gameListener = onGameListener;
        this.gameArray = gameArray;
    }

    @NonNull
    @Override
    public GameAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_display_game_list,
                parent, false);
        return new ViewHolder(view, gameListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(GameAdapter.ViewHolder holder, int position) {
        Game game = gameArray.get(position);
        holder.gameDate.setText(game.getCreationDate());
        holder.groupScore.setText(context.getString(R.string.game_type_group_score, game.getSumScores()));
        holder.numPlayers.setText(context.getString(R.string.game_type_number_players, game.getNumPlayer()));
        holder.achievementLevel.setText(game.getAchievementLevel().getValue());
        holder.achievementImage.setImageResource(context.getResources().getIdentifier(
                gameTypeManager.determineThemeSelected() + (AchievementLevels.NUM_LEVELS - game.getAchievementLevel().ordinal()),
                context.getString(R.string.drawable), context.getPackageName()));
        if (game.getEncodedPhoto() != null) {
            holder.gamePhoto.setImageBitmap(game.getGamePhoto());
        }
        if(game.getDifficulty().equals("Easy")){
            holder.difficultyLevel.setText(game.getDifficulty());
            holder.difficultyLevel.setTextColor(Color.GREEN);
        }
        if(game.getDifficulty().equals("Normal")){
            holder.difficultyLevel.setText(game.getDifficulty());
            holder.difficultyLevel.setTextColor(Color.rgb(225,173,1));
        }
        if(game.getDifficulty().equals("Hard")){
            holder.difficultyLevel.setText(game.getDifficulty());
            holder.difficultyLevel.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return gameArray.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView achievementImage;
        private final ImageView gamePhoto;
        private final TextView gameDate;
        private final TextView achievementLevel;
        private final TextView groupScore;
        private final TextView numPlayers;
        private final TextView difficultyLevel;
        private final OnGameListener onGameListener;

        public ViewHolder(View itemView, OnGameListener onGameListener) {
            super(itemView);
            achievementImage = itemView.findViewById(R.id.ivAchievementImage);
            gamePhoto = itemView.findViewById(R.id.ivGamePhoto);
            gameDate = itemView.findViewById(R.id.tvGameDate);
            achievementLevel = itemView.findViewById(R.id.tvAchievementLevel);
            groupScore = itemView.findViewById(R.id.tvGroupScore);
            numPlayers = itemView.findViewById(R.id.tvNumPlayers);
            difficultyLevel = itemView.findViewById(R.id.difficulty);
            this.onGameListener = onGameListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onGameListener.onGameClick(getAdapterPosition());
        }
    }

    public interface OnGameListener {
        void onGameClick(int position);
    }
}

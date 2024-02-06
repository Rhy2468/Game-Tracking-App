package ca.cmpt276.coopachievement.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.cmpt276.coopachievement.R;
import ca.cmpt276.coopachievement.model.GameType;

/**
 * Handles game type list logic
 */
public class GameTypeAdapter extends RecyclerView.Adapter<GameTypeAdapter.ViewHolder> {

    private final Context context;
    private final OnGameTypeListener onClickListener;
    private final ArrayList<GameType> gameTypeArray;

    public GameTypeAdapter(Context context, OnGameTypeListener onGameTypeListener, ArrayList<GameType> gameTypeArray) {
        this.context = context;
        this.onClickListener = onGameTypeListener;
        this.gameTypeArray = gameTypeArray;
    }

    @NonNull
    @Override
    public GameTypeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_display_game_types,
                parent, false);
        return new ViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GameType gameType = gameTypeArray.get(position);
        holder.gameTypeName.setText(gameType.getName());
        holder.gameTypePoor.setText(context.getString(R.string.home_poor_expected_score,
                gameType.getExpectedPoorScore()));
        holder.gameTypeGreat.setText(context.getString(R.string.home_great_expected_score,
                gameType.getExpectedGreatScore()));
        if (gameType.getImageSelectedIndex() == -1) {
            Bitmap photoTaken = gameType.getPhotoTaken();
            holder.gameTypeImage.setImageBitmap(photoTaken);
        } else {
            holder.gameTypeImage.setImageResource(context.getResources().getIdentifier(
                    context.getString(R.string.icon_prefix) + gameType.getImageSelectedIndex(),
                    context.getString(R.string.drawable), context.getPackageName()));
        }
    }

    @Override
    public int getItemCount() {
        return gameTypeArray.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ImageView gameTypeImage;
        private final TextView gameTypeName;
        private final TextView gameTypePoor;
        private final TextView gameTypeGreat;

        public ViewHolder(View itemView, OnGameTypeListener onClickListener) {
            super(itemView);
            gameTypeImage = itemView.findViewById(R.id.ivGameTypeImage);
            gameTypeName = itemView.findViewById(R.id.tvGameType);
            gameTypePoor = itemView.findViewById(R.id.tvPoorExpectedScore);
            gameTypeGreat = itemView.findViewById(R.id.tvGreatExpectedScore);

            ImageButton gameDeleteType = itemView.findViewById(R.id.btnRemoveType);

            itemView.setOnClickListener(view -> onClickListener.onGameTypeClick(getAdapterPosition()));
            gameDeleteType.setOnClickListener(view -> onClickListener.onGameTypeDeleteClick(getAdapterPosition()));
        }
    }

    public interface OnGameTypeListener {
        void onGameTypeClick(int position);
        void onGameTypeDeleteClick(int position);
    }
}

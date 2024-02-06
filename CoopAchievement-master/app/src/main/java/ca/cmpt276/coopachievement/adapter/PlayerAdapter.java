package ca.cmpt276.coopachievement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ca.cmpt276.coopachievement.R;

/**
 * Handles player list logic
 */

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<Integer> playerArray;

    public PlayerAdapter(Context context, ArrayList<Integer> playerArray) {
        this.context = context;
        this.playerArray = playerArray;
    }

    @NonNull
    @Override
    public PlayerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_display_players,
                parent, false);
        return new ViewHolder(view, new PlayerScoreListener());
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(PlayerAdapter.ViewHolder holder, int position) {
        holder.playerName.setText(context.getString(R.string.player_name, position+1));
        holder.playerScoreListener.updatePosition(holder.getAdapterPosition());
        if (playerArray.get(position) == 0) {
            holder.playerScore.setText("");
        } else {
            holder.playerScore.setText(String.valueOf(playerArray.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return playerArray.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView playerName;
        private final EditText playerScore;
        private final PlayerScoreListener playerScoreListener;

        public ViewHolder(View itemView, PlayerScoreListener playerScoreListener) {
            super(itemView);
            playerName = itemView.findViewById(R.id.tvPlayerName);
            playerScore = itemView.findViewById(R.id.etPlayerScoreInput);
            this.playerScoreListener = playerScoreListener;
            playerScore.addTextChangedListener(playerScoreListener);
        }
    }

    private class PlayerScoreListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!charSequence.toString().equals("0") && !charSequence.toString().isEmpty()) {
                playerArray.set(position, Integer.parseInt(charSequence.toString()));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    }
}

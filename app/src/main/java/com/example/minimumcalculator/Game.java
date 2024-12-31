package com.example.minimumcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Game extends AppCompatActivity {
    private ArrayList<Player> players;
    private LinearLayout linearLayoutGame;
    private ScrollView scrollViewGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws RuntimeException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);
        linearLayoutGame = findViewById(R.id.linearLayoutGame);
        scrollViewGame = findViewById(R.id.scrollViewGame);
        Intent intent = getIntent();
        players = intent.getParcelableArrayListExtra("players");
        populatePlayers(players);
    }

    public void onClickAddRoundScore(View view){
        Intent intent=new Intent(Game.this, RoundScores.class);
        intent.putParcelableArrayListExtra("players", this.players);
        startActivity(intent);
        finish();
    }

    public void onClickViewStats(View view) {
        Intent intent=new Intent(Game.this, ViewStats.class);
        intent.putParcelableArrayListExtra("players", this.players);
        startActivity(intent);
    }

    public void onClickFinishGame(View view) {
        Intent intent = new Intent(Game.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void populatePlayers(List<Player> players) {
        linearLayoutGame.removeAllViews();
        for (Player player : players) {
            View playerRow = getLayoutInflater().inflate(R.layout.player_row, linearLayoutGame, false);

            TextView playerNameTextView = playerRow.findViewById(R.id.textPlayerName);
            TextView playerScoreTextView = playerRow.findViewById(R.id.textPlayerScore);

            playerNameTextView.setText(player.getName());
            playerScoreTextView.setText(String.valueOf(player.getScore()));

            linearLayoutGame.addView(playerRow);
        }
        updateScrollViewHeight();
    }

    private void updateScrollViewHeight() {
        int totalHeight = 0;
        for (int i = 0; i < linearLayoutGame.getChildCount(); i++) {
            View child = linearLayoutGame.getChildAt(i);
            child.measure(0, 0);
            totalHeight += child.getMeasuredHeight();
        }
        scrollViewGame.getLayoutParams().height = totalHeight;
        scrollViewGame.requestLayout();

        LinearLayout parentLayout = (LinearLayout) scrollViewGame.getParent();
        parentLayout.post(() -> {
            parentLayout.requestLayout();
            parentLayout.invalidate();
        });
    }

    @Override
    public void onBackPressed() {
    }


    // Extra

    public void printStats() {
        for(Player player : players) {
            for (int s : player.getStats()) {
                System.out.print(s);
                System.out.print(" ");
            }
            System.out.println();
        }
    }
    private String[] getPlayerNames(List<Player> players) {
        ArrayList<String> playerNames = new ArrayList<>();
        for(Player player : players) {
            playerNames.add(player.getName());
        }
        return playerNames.toArray(new String[0]);
    }

    private int[] getPlayerScores(List<Player> players) {
        ArrayList<Integer> playerScores = new ArrayList<>();
        for(Player player : players) {
            playerScores.add(player.getScore());
        }
        return playerScores.stream().mapToInt(Integer::intValue).toArray();
    }

    public List<Player> getPlayersFromNamesAndScores(String[] playerNames, int[] scores) {
        List<Player> players = new ArrayList<>();
        int n = playerNames.length;
        for(int i=0;i<n;i++) {
            players.add(new Player(playerNames[i], scores[i]));
        }
        return players;
    }
}

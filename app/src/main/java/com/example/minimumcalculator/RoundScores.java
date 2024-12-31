package com.example.minimumcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoundScores extends AppCompatActivity {
    private ArrayList<Player> players;
    private LinearLayout linearLayoutAddScores;
    private ScrollView scrollViewAddScores;
    private Map<Player, EditText> playerEditTextMap = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_scores);
        linearLayoutAddScores = findViewById(R.id.linearLayoutAddScores);
        scrollViewAddScores = findViewById(R.id.scrollViewAddScores);
        Intent intent = getIntent();
        players = intent.getParcelableArrayListExtra("players");
        populatePlayers(players);

        Button addScores = findViewById(R.id.add);
        addScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RoundScores.this, Game.class);
                updatePlayerScores();
                intent.putParcelableArrayListExtra("players", players);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updatePlayerScores() {
        Map<Player, String> playerScoresMap = new HashMap<>();
        for (Map.Entry<Player, EditText> entry : playerEditTextMap.entrySet()) {
            Player player = entry.getKey();
            EditText editText = entry.getValue();
            String score = editText.getText().toString().trim();
            playerScoresMap.put(player, score);
        }

        for (Player player : players) {
            int oldScore = player.getScore();
            String inputText = playerScoresMap.get(player);
            int enteredValue = inputText.isEmpty() ? 0 : Integer.parseInt(inputText);
            player.addToStats(enteredValue);
            player.setScore(oldScore + enteredValue);
        }
    }

    private void populatePlayers(List<Player> players) {
        for (Player player : players) {
            View playerRow = getLayoutInflater().inflate(R.layout.player_row_add_score, linearLayoutAddScores, false);

            TextView playerNameTextView = playerRow.findViewById(R.id.playerNameAddRound);
            EditText playerScoreEditText = playerRow.findViewById(R.id.playerScoreAddRound);

            playerNameTextView.setText(player.getName());
            playerEditTextMap.put(player, playerScoreEditText);

            linearLayoutAddScores.addView(playerRow);
        }
        updateScrollViewHeight();
    }

    private void updateScrollViewHeight() {
        int totalHeight = 0;
        for (int i = 0; i < linearLayoutAddScores.getChildCount(); i++) {
            View child = linearLayoutAddScores.getChildAt(i);
            child.measure(0, 0);
            totalHeight += child.getMeasuredHeight();
        }
        scrollViewAddScores.getLayoutParams().height = totalHeight;
        scrollViewAddScores.requestLayout();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RoundScores.this, Game.class);
        intent.putParcelableArrayListExtra("players", players);
        startActivity(intent);
        finish();
    }


    // Extra
    private String[] getPlayerNames(List<Player> players) {
        ArrayList<String> playerNames = new ArrayList<>();
        for(Player player : players) {
            playerNames.add(player.getName());
        }
        return playerNames.toArray(new String[0]);
    }

    public List<Player> getPlayersFromNamesAndScores(String[] playerNames, int[] scores) {
        List<Player> players = new ArrayList<>();
        int n = playerNames.length;
        for(int i=0;i<n;i++) {
            players.add(new Player(playerNames[i], scores[i]));
        }
        return players;
    }

    private int[] getPlayerScores(List<Player> players) {
        Map<Player, String> playerScoresMap = new HashMap<>();
        for (Map.Entry<Player, EditText> entry : playerEditTextMap.entrySet()) {
            Player player = entry.getKey();
            EditText editText = entry.getValue();
            String score = editText.getText().toString().trim();
            playerScoresMap.put(player, score);
        }

        ArrayList<Integer> playerScores = new ArrayList<>();
        for (Player player : players) {
            int oldScore = player.getScore();
            String inputText = playerScoresMap.get(player);
            int enteredValue = inputText.isEmpty() ? 0 : Integer.parseInt(inputText);
            playerScores.add(oldScore + enteredValue);
        }
        return playerScores.stream().mapToInt(Integer::intValue).toArray();
    }

}
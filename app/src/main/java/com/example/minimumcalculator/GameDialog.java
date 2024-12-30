package com.example.minimumcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;

public class GameDialog extends AppCompatActivity {
    private int playerCount = 2;
    private LinearLayout linearLayoutPlayers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_dialog);

        linearLayoutPlayers = findViewById(R.id.linearLayoutPlayers);
        Button btnAddPlayer = findViewById(R.id.btnAddPlayer);

        btnAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPlayer();
            }
        });

        Button startGame=findViewById(R.id.startGame);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameDialog.this, Game.class);
                String[] playerNames = getPlayerNames();
                intent.putExtra("players", playerNames);
                ArrayList<Integer> scores = new ArrayList<>();
                for(int i=0; i < playerNames.length; i++) {
                    scores.add(0);
                }
                intent.putExtra("scores", scores.stream().mapToInt(Integer::intValue).toArray());
                startActivity(intent);
                finish();
            }
        });
    }

    private void addNewPlayer() {
        // Increment player count
        playerCount++;

        // Create a new EditText for the new player
        EditText newPlayerEditText = new EditText(this);
        newPlayerEditText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        newPlayerEditText.setHint("P" + playerCount);

        // Add margin below the new EditText
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) newPlayerEditText.getLayoutParams();
        params.setMargins(0, 0, 0, 16);
        newPlayerEditText.setLayoutParams(params);

        // Add the new EditText above the "Add Player" button
        int addPlayerButtonIndex = linearLayoutPlayers.indexOfChild(findViewById(R.id.btnAddPlayer));
        linearLayoutPlayers.addView(newPlayerEditText, addPlayerButtonIndex);

        // Scroll to the bottom to make the new player visible
        final ScrollView scrollView = (ScrollView) linearLayoutPlayers.getParent();
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    public String[] getPlayerNames() {
        ArrayList<String> playerNames = new ArrayList<>();
        int childCount = linearLayoutPlayers.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = linearLayoutPlayers.getChildAt(i);
            if (child instanceof EditText) {
                EditText playerField = (EditText) child;
                String playerName = playerField.getText().toString().trim();
                if (!playerName.isEmpty()) {
                    playerNames.add(playerName);
                } else {
                    playerNames.add(playerField.getHint().toString());
                }
            }
        }

        return playerNames.toArray(new String[0]);
    }

}
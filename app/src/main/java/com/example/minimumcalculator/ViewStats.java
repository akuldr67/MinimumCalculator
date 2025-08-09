package com.example.minimumcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.text.Editable;
import android.text.TextWatcher;

public class ViewStats extends AppCompatActivity {
    private ArrayList<Player> players;
    private int currentTurn;
    private Map<Player, ArrayList<EditText> > playerStatsEditTextMap = new HashMap<>();
    // Track total TextView for each player to update in real time
    private Map<Player, TextView> playerTotalTextViewMap = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_stats);

        Intent intent = getIntent();
        players = intent.getParcelableArrayListExtra("players");
        currentTurn = intent.getIntExtra("currentTurn", 0);
        TableLayout tableLayout = findViewById(R.id.dynamic_table);

        int numOfRounds = players.get(0).getStats().size();

        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));
        TextView header = new TextView(this);
        header.setText("Player");
        setTextViewSettings(header);
        headerRow.addView(header);
        for (int i=1;i<=numOfRounds;i++) {
            TextView rHeader = new TextView(this);
            rHeader.setText("R" + String.valueOf(i));
            setTextViewSettings(rHeader);
            headerRow.addView(rHeader);
        }
        TextView totalHeader = new TextView(this);
        totalHeader.setText("Total");
        setTextViewSettings(totalHeader);
        headerRow.addView(totalHeader);
        tableLayout.addView(headerRow);

        for(Player player: players) {
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            ));
            TextView playerHeader = new TextView(this);
            playerHeader.setText(player.getName());
            setTextViewSettings(playerHeader);
            tableRow.addView(playerHeader);
            playerStatsEditTextMap.put(player, new ArrayList<>());
            final Player currentPlayer = player;

            for (int cell : player.getStats()) {
                EditText textView = new EditText(this);
                textView.setInputType(InputType.TYPE_CLASS_NUMBER);
                textView.setText(String.valueOf(cell));
                setTextViewSettings(textView);
                tableRow.addView(textView);
                playerStatsEditTextMap.get(player).add(textView);
                textView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }

                    @Override
                    public void afterTextChanged(Editable s) {
                        recalcAndDisplayTotal(currentPlayer);
                    }
                });
            }

            TextView playerTotalView = new TextView(this);
            playerTotalView.setText(String.valueOf(player.getScore()));
            setTextViewSettings(playerTotalView);
            playerTotalTextViewMap.put(player, playerTotalView);
            tableRow.addView(playerTotalView);

            tableLayout.addView(tableRow);
        }

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button updateScores = findViewById(R.id.update_stats_button);
        updateScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewStats.this, Game.class);
                updatePlayerScores();
                intent.putParcelableArrayListExtra("players", players);
                intent.putExtra("currentTurn", currentTurn);
                startActivity(intent);
                finish();
            }
        });
    }

    private void updatePlayerScores() {
        for (Map.Entry<Player, ArrayList<EditText>> entry : playerStatsEditTextMap.entrySet()) {
            Player player = entry.getKey();
            int playerOldScore = player.getScore();
            ArrayList<Integer> playerStats = player.getStats();

            ArrayList<EditText> playerStatsEditText = entry.getValue();
            for (int i=0; i<playerStatsEditText.size(); i++) {
                EditText editText = playerStatsEditText.get(i);
                String text = editText.getText().toString().trim();
                Integer newRoundScore = !text.isEmpty() ? Integer.parseInt(text): 0;
                int diffInScore = newRoundScore - playerStats.get(i);
                playerStats.set(i, newRoundScore);
                int newScore = playerOldScore + diffInScore;
                player.setScore(newScore);
                playerOldScore = newScore;
            }
        }
    }

    private void recalcAndDisplayTotal(Player player) {
        int total = 0;
        ArrayList<EditText> edits = playerStatsEditTextMap.get(player);
        if (edits == null) return;
        for (EditText et : edits) {
            String text = et.getText().toString().trim();
            if (text.isEmpty()) {
                continue;
            }
            try {
                total += Integer.parseInt(text);
            } catch (NumberFormatException ignored) { }
        }
        TextView totalView = playerTotalTextViewMap.get(player);
        if (totalView != null) {
            totalView.setText(String.valueOf(total));
        }
    }

    public void setTextViewSettings(TextView textView) {
        textView.setPadding(16, 16, 16, 16);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));
    }
}
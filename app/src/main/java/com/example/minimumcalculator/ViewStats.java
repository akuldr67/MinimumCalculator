package com.example.minimumcalculator;

import android.content.Intent;
import android.os.Bundle;
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

public class ViewStats extends AppCompatActivity {
    private ArrayList<Player> players;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_stats);

        players = getIntent().getParcelableArrayListExtra("players");
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

            for (int cell : player.getStats()) {
                TextView textView = new TextView(this);
                textView.setText(String.valueOf(cell));
                setTextViewSettings(textView);
                tableRow.addView(textView);
            }

            TextView playerTotalView = new TextView(this);
            playerTotalView.setText(String.valueOf(player.getScore()));
            setTextViewSettings(playerTotalView);
            tableRow.addView(playerTotalView);

            tableLayout.addView(tableRow);
        }

        Button startGame=findViewById(R.id.back_button);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
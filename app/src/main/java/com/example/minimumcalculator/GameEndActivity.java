package com.example.minimumcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class GameEndActivity extends AppCompatActivity {
    private ArrayList<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);
        Intent intent = getIntent();
        players = intent.getParcelableArrayListExtra("players");
    }

    public void onClickGameEndReplay(View view){
        Intent intent = new Intent(GameEndActivity.this, Game.class);
        for (Player player : players) {
            player.setScore(0);
            player.setStats(new ArrayList<>());
        }
        intent.putParcelableArrayListExtra("players", players);
        startActivity(intent);
        finish();
    }

    public void onClickGameEndNewGame(View view){
        Intent intent=new Intent(GameEndActivity.this, GameDialog.class);
        startActivity(intent);
        finish();
    }

    public void onClickGameEndViewPlayerStats(View view) {
        Intent intent = new Intent(GameEndActivity.this, ViewPlayerStats.class);
        startActivity(intent);
    }
}
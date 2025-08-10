package com.example.minimumcalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewPlayerStats extends AppCompatActivity {

    private static final String PREFS_NAME = "player_prefs";
    private static final String KEY_OVERALL_STATS = "overall_stats";

    // Helper row model to support sorting
    private static final class PlayerStatsRow {
        final String playerName;
        final int played;
        final int won;
        final int lost;

        PlayerStatsRow(String playerName, int played, int won, int lost) {
            this.playerName = playerName;
            this.played = played;
            this.won = won;
            this.lost = lost;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_player_stats);

        TableLayout tableLayout = findViewById(R.id.player_stats_table);
        addHeaderRow(tableLayout);
        populateTable(tableLayout);
    }

    public void onClickBack(View view) {
        Intent intent = new Intent(ViewPlayerStats.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void addHeaderRow(TableLayout tableLayout) {
        TableRow headerRow = new TableRow(this);
        headerRow.addView(makeCell("Player", true));
        headerRow.addView(makeCell("Played", true));
        headerRow.addView(makeCell("Won", true));
        headerRow.addView(makeCell("Lost", true));
        tableLayout.addView(headerRow);
    }

    private void populateTable(TableLayout tableLayout) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_OVERALL_STATS, "{}");
        JSONObject root;
        try {
            root = new JSONObject(json);
        } catch (JSONException e) {
            root = new JSONObject();
        }

        // Collect rows first
        List<PlayerStatsRow> rows = new ArrayList<>();
        Iterator<String> keys = root.keys();
        while (keys.hasNext()) {
            String playerName = keys.next();
            JSONObject stats = root.optJSONObject(playerName);
            if (stats == null) continue;
            int played = stats.optInt("played", 0);
            int lost = stats.optInt("lost", 0);
            int won = Math.max(0, played - lost);
            rows.add(new PlayerStatsRow(playerName, played, won, lost));
        }

        // Sort: played desc, then name asc
        Collections.sort(rows, new Comparator<PlayerStatsRow>() {
            @Override
            public int compare(PlayerStatsRow a, PlayerStatsRow b) {
                if (a.played != b.played) {
                    return Integer.compare(b.played, a.played);
                }
                return a.playerName.compareToIgnoreCase(b.playerName);
            }
        });

        // Render sorted rows
        for (PlayerStatsRow rowData : rows) {
            TableRow row = new TableRow(this);
            row.addView(makeCell(rowData.playerName, false));
            row.addView(makeCell(String.valueOf(rowData.played), false));
            row.addView(makeCell(String.valueOf(rowData.won), false));
            row.addView(makeCell(String.valueOf(rowData.lost), false));
            tableLayout.addView(row);
        }
    }

    private TextView makeCell(String text, boolean isHeader) {
        TextView tv = new TextView(this);
        tv.setText(text);
        int pad = (int) (8 * getResources().getDisplayMetrics().density);
        tv.setPadding(pad, pad, pad, pad);
        if (isHeader) {
            tv.setTypeface(tv.getTypeface(), android.graphics.Typeface.BOLD);
        }
        return tv;
    }
} 
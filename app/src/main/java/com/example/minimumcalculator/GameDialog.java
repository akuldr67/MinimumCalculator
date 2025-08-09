package com.example.minimumcalculator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class GameDialog extends AppCompatActivity {
    private int playerCount = 2;
    private LinearLayout linearLayoutPlayers;

    private static final String PREFS_NAME = "player_prefs";
    private static final String KEY_PLAYER_NAMES = "player_names";

    private SharedPreferences sharedPreferences;
    private final List<String> allPlayerNames = new ArrayList<>();
    private PrefixArrayAdapter namesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_dialog);

        linearLayoutPlayers = findViewById(R.id.linearLayoutPlayers);
        Button btnAddPlayer = findViewById(R.id.btnAddPlayer);

        // Init preferences and adapter
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        loadStoredNames();
        namesAdapter = new PrefixArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, allPlayerNames);

        // Wire autocomplete to existing fields
        setupAutoCompleteForAllPlayerFields();

        btnAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewPlayer();
            }
        });

        Button startGame = findViewById(R.id.startGame);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Persist any new names used in this game
                persistNamesIfNew(getTypedPlayerNames());

                Intent intent = new Intent(GameDialog.this, Game.class);
                intent.putParcelableArrayListExtra("players", getPlayers());
                startActivity(intent);
                finish();
            }
        });
    }

    private void addNewPlayer() {
        // Increment player count
        playerCount++;

        // Create a new AutoCompleteTextView for the new player
        AutoCompleteTextView newPlayerEditText = new AutoCompleteTextView(this);
        newPlayerEditText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        newPlayerEditText.setHint("P" + playerCount);

        // Add margin below the new field
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) newPlayerEditText.getLayoutParams();
        params.setMargins(0, 0, 0, 16);
        newPlayerEditText.setLayoutParams(params);

        // Setup autocomplete behavior
        setupAutoCompleteForField(newPlayerEditText);

        // Add the new field above the "Add Player" button
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

    public ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        int childCount = linearLayoutPlayers.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = linearLayoutPlayers.getChildAt(i);
            if (child instanceof EditText) {
                EditText playerField = (EditText) child;
                String playerName = playerField.getText().toString().trim();
                if (!playerName.isEmpty()) {
                    players.add(new Player(playerName));
                } else {
                    players.add(new Player(playerField.getHint().toString()));
                }
            }
        }

        return players;
    }

    // Extra

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

    private String[] getTypedPlayerNames() {
        ArrayList<String> names = new ArrayList<>();
        int childCount = linearLayoutPlayers.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = linearLayoutPlayers.getChildAt(i);
            if (child instanceof EditText) {
                EditText playerField = (EditText) child;
                String playerName = playerField.getText().toString().trim();
                if (!playerName.isEmpty()) {
                    names.add(playerName);
                }
            }
        }
        return names.toArray(new String[0]);
    }

    private void setupAutoCompleteForAllPlayerFields() {
        int childCount = linearLayoutPlayers.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = linearLayoutPlayers.getChildAt(i);
            if (child instanceof AutoCompleteTextView) {
                setupAutoCompleteForField((AutoCompleteTextView) child);
            }
        }
    }

    private void setupAutoCompleteForField(@NonNull AutoCompleteTextView field) {
        field.setThreshold(3);
        field.setAdapter(namesAdapter);
        field.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    AutoCompleteTextView actv = (AutoCompleteTextView) v;
                    if (actv.getText().length() > 0) {
                        actv.showDropDown();
                    }
                }
            }
        });
    }

    private void loadStoredNames() {
        Set<String> stored = sharedPreferences.getStringSet(KEY_PLAYER_NAMES, new HashSet<String>());
        allPlayerNames.clear();
        allPlayerNames.addAll(stored);
    }

    private void persistNamesIfNew(@NonNull String[] names) {
        // Load current set and build a case-insensitive presence set
        Set<String> current = new HashSet<>(sharedPreferences.getStringSet(KEY_PLAYER_NAMES, new HashSet<String>()));
        Set<String> currentLower = new HashSet<>();
        for (String n : current) {
            if (n != null) currentLower.add(n.toLowerCase(Locale.ROOT));
        }

        boolean changed = false;
        for (String name : names) {
            if (name == null) continue;
            String trimmed = name.trim();
            if (trimmed.isEmpty()) continue;
            String lower = trimmed.toLowerCase(Locale.ROOT);
            if (!currentLower.contains(lower)) {
                current.add(trimmed);
                currentLower.add(lower);
                changed = true;
            }
        }

        if (changed) {
            sharedPreferences.edit().putStringSet(KEY_PLAYER_NAMES, current).apply();
            // Update in-memory list and adapter dataset
            loadStoredNames();
            if (namesAdapter != null) {
                namesAdapter.updateData(allPlayerNames);
            }
        }
    }

    private static class PrefixArrayAdapter extends ArrayAdapter<String> {
        private final Object lock = new Object();
        private List<String> originalData;

        PrefixArrayAdapter(@NonNull GameDialog context, int resource, @NonNull List<String> items) {
            super(context, resource, new ArrayList<>(items));
            originalData = new ArrayList<>(items);
        }

        void updateData(@NonNull List<String> items) {
            synchronized (lock) {
                originalData = new ArrayList<>(items);
                clear();
                addAll(items);
                notifyDataSetChanged();
            }
        }

        @Override
        public android.widget.Filter getFilter() {
            return new android.widget.Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence prefix) {
                    FilterResults results = new FilterResults();
                    List<String> source;
                    synchronized (lock) {
                        source = new ArrayList<>(originalData);
                    }

                    if (prefix == null || prefix.length() == 0) {
                        results.values = source;
                        results.count = source.size();
                        return results;
                    }

                    String prefixString = prefix.toString().toLowerCase(Locale.ROOT);
                    List<String> filtered = new ArrayList<>();
                    for (String item : source) {
                        if (item != null && item.toLowerCase(Locale.ROOT).startsWith(prefixString)) {
                            filtered.add(item);
                        }
                    }
                    results.values = filtered;
                    results.count = filtered.size();
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    clear();
                    if (results != null && results.values instanceof List) {
                        //noinspection unchecked
                        addAll((List<String>) results.values);
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
        }
    }
}
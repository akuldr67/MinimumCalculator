package com.example.minimumcalculator;

import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;

public class Player implements Parcelable {
    private String name;
    private int score = 0;
    private ArrayList<Integer> stats;

    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.stats = new ArrayList<>();
    }

    public Player(String name, int score) {
        this.score = score;
        this.name = name;
        this.stats = new ArrayList<>();
    }

    public Player(String name, int score, ArrayList<Integer> stats) {
        this.name = name;
        this.score = score;
        this.stats = stats;
    }

    protected Player(Parcel in) {
        name = in.readString();
        score = in.readInt();
        stats = new ArrayList<>();
        in.readList(stats, Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(score);
        dest.writeList(stats);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setStats(ArrayList<Integer> stats) {
        this.stats = stats;
    }

    public ArrayList<Integer> getStats() {
        return this.stats;
    }

    public void addToStats(int s) {
        this.stats.add(s);
    }
}

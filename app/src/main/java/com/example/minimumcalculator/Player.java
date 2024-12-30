package com.example.minimumcalculator;

public class Player {
    private String name;
    private int score = 0;

    public Player(String name) {
        this.name = name;
        this.score = 0;
    }

    public Player(String name, int score) {
        this.score = score;
        this.name = name;
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
}

package com.jjjackson.konchinka.domain;

public class GameResult {

    public int total;

    public int ace;
    public int clubsTwo;
    public int diamondsTen;
    public int clubs;
    public int cards;
    public int tricks;

    public void clear() {
        this.ace = 0;
        this.clubsTwo = 0;
        this.diamondsTen = 0;
        this.clubs = 0;
        this.cards = 0;
        this.tricks = 0;
    }
}

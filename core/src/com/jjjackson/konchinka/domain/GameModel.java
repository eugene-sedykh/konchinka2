package com.jjjackson.konchinka.domain;

import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
    public Pack pack;
    public List<Card> cards = new ArrayList<>();
    public States states;
    public Table table;
    public List<User> opponents = new ArrayList<>();
    public User player;
    public List<CardHolder> cardHolders;
    public CardHolder currentPlayer;
    public CardPosition dealerPosition;
    public Buttons buttons = new Buttons();
    public Fog fog;
    public Group fogGroup;
    public Card playCard;
}

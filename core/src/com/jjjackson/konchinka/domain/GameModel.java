package com.jjjackson.konchinka.domain;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

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
    public Card playCard;
    public List<Card> turnCombinedCards = new ArrayList<>();
    public boolean isTrickTaken;
    public Skin skin;
    public Stage stage;
}

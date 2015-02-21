package com.jjjackson.konchinka.domain;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

public class GameModel {
    public Pack pack;
    public States states;
    public Table table;
    public Array<User> opponents = new Array<>();
    public User player;
    public Array<CardHolder> cardHolders;
    public CardHolder currentPlayer;
    public CardPosition dealerPosition;
    public Buttons buttons = new Buttons();
    public Fog fog;
    public Card playCard;
    public List<Card> turnCombinedCards = new ArrayList<>();
    public boolean isTrickTaken;
    public Skin skin;
    public Stage stage;
    public int turnCount = 1;
    public BitmapFont font;
}

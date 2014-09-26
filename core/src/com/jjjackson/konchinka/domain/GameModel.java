package com.jjjackson.konchinka.domain;

import com.jjjackson.konchinka.handler.GameObjectHandler;

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
}

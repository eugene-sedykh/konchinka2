package com.jjjackson.konchinka.domain;

public class States {

    public GameState game = GameState.NONE;
    public TurnState turn = TurnState.NONE;
    public DealState deal = DealState.PACK_IN;

    public boolean isFaded;

    public boolean isSortButtonShown;
    public boolean isSortDoneButtonShown;
    public boolean isDoneButtonShown;
    public boolean hasTrick;
}

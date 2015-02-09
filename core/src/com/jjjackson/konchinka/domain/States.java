package com.jjjackson.konchinka.domain;

import com.jjjackson.konchinka.domain.state.*;

public class States {

    public GameState game = GameState.GAME_RESULT;
    public TurnState turn = TurnState.INIT_BUTTONS;
    public DealState deal = DealState.PACK_IN;
    public CpuTurn cpuTurn = CpuTurn.NONE;
    public ResultState result = ResultState.INIT;
    public NewGameState newGame = NewGameState.INIT;
}

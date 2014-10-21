package com.jjjackson.konchinka.domain;

import com.jjjackson.konchinka.domain.state.CpuTurn;
import com.jjjackson.konchinka.domain.state.DealState;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.domain.state.TurnState;

public class States {

    public GameState game = GameState.NONE;
    public TurnState turn = TurnState.NONE;
    public DealState deal = DealState.PACK_IN;
    public CpuTurn cpuTurn = CpuTurn.NONE;

    public boolean isFaded;
    public boolean isSortButtonShown;
    public boolean isSortDoneButtonShown;
    public boolean isDoneButtonShown;
    public boolean hasTrick;
}

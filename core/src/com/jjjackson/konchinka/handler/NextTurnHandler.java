package com.jjjackson.konchinka.handler;

import com.badlogic.gdx.utils.Array;
import com.jjjackson.konchinka.domain.CardHolder;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.state.DealState;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.objectmover.ObjectMover;
import com.jjjackson.konchinka.util.PlayerUtil;

public class NextTurnHandler extends GameObjectHandler {

    public NextTurnHandler(GameModel model, ObjectMover objectMover) {
        super(model, objectMover);
    }

    @Override
    public void handle() {
        switchPlayer();

        if (PlayerUtil.wasLastTurn(model)) {
            model.states.game = GameState.GAME_RESULT;
            PlayerUtil.disablePlayer(model.currentPlayer);
            return;
        }

        if (needToDeal(model.cardHolders)) {
            model.states.game = GameState.DEAL;
            model.states.deal = DealState.PACK_IN;
            model.turnCount++;
            return;
        }

        model.states.game = GameState.TURN;
    }

    private void switchPlayer() {
        PlayerUtil.disablePlayer(model.currentPlayer);
        PlayerUtil.switchPlayer(model);
    }

    private boolean needToDeal(Array<CardHolder> cardHolders) {
        for (CardHolder cardHolder : cardHolders) {
            if (!cardHolder.playCards.isEmpty()) {
                return false;
            }
        }

        return true;
    }
}

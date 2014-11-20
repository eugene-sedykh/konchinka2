package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.TweenManager;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.CardHolder;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.User;
import com.jjjackson.konchinka.domain.state.DealState;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.util.PlayerUtil;

import java.util.List;

public class NextTurnHandler extends GameObjectHandler {

    public NextTurnHandler(GameModel model, TweenManager tweenManager) {
        super(model, tweenManager);
    }

    @Override
    public void handle() {
        if (isLastTurn()) {
            this.model.states.game = GameState.CALC_RESULT;
            return;
        }

        switchPlayer();

        if (needToDeal(this.model.cardHolders)) {
            this.model.states.game = GameState.DEAL;
            this.model.states.deal = DealState.PACK_IN;
            this.model.turnCount++;
            return;
        }

        this.model.states.game = GameState.TURN;
    }

    private void switchPlayer() {
        PlayerUtil.disablePlayer(this.model.currentPlayer);
        PlayerUtil.switchPlayer(this.model);
    }

    private boolean needToDeal(List<CardHolder> cardHolders) {
        for (CardHolder cardHolder : cardHolders) {
            if (!cardHolder.playCards.isEmpty()) {
                return false;
            }
        }

        return true;
    }
}

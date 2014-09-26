package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.TweenManager;
import com.jjjackson.konchinka.domain.CardHolder;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.GameState;
import com.jjjackson.konchinka.util.PlayerUtil;

public class NextTurnHandler extends GameObjectHandler {

    public NextTurnHandler(GameModel model, TweenManager tweenManager) {
        super(model, tweenManager);
    }

    @Override
    public void handle() {
        PlayerUtil.switchPlayer(this.model);
        this.model.states.game = GameState.TURN;
    }
}

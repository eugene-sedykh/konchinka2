package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.TweenManager;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.CardHolder;
import com.jjjackson.konchinka.domain.GameModel;

public abstract class GameObjectHandler {

    protected final GameModel model;
    protected final TweenManager tweenManager;
    protected Card movingCard;

    public GameObjectHandler(GameModel model, TweenManager tweenManager) {
        this.model = model;
        this.tweenManager = tweenManager;
    }

    public abstract void handle();

    protected CardHolder getCurrentPlayer() {
        for (CardHolder cardHolder : this.model.cardHolders) {
            if (cardHolder.isCurrent) {
                return cardHolder;
            }
        }
        return null;
    }
}

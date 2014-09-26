package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.TweenManager;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.CardHolder;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.util.CardCombinator;
import com.jjjackson.konchinka.util.CardMover;

public abstract class GameObjectHandler {

    protected final GameModel model;
    protected final TweenManager tweenManager;
    protected Card movingCard;
    protected CardCombinator cardCombinator;
    protected CardMover cardMover;

    public GameObjectHandler(GameModel model, TweenManager tweenManager) {
        this.model = model;
        this.tweenManager = tweenManager;
        this.cardCombinator = new CardCombinator();
        this.cardMover = new CardMover();
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

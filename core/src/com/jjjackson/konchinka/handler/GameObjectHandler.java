package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.util.CardCombinator;
import com.jjjackson.konchinka.util.CardMover;
import com.jjjackson.konchinka.util.PositionCalculator;

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

    protected void moveCardToTable(final Card card) {
        this.cardMover.changeCenterCardsPosition(this.model.table.playCards, false);
        Point destination = new Point();
        PositionCalculator.calcCenter(this.model.table.playCards.size(), destination);
        card.toFront();
        Tween.to(card, GameObject.POSITION_XY, 0.2f).
                target(destination.x, destination.y).
                start(this.tweenManager).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if (type != COMPLETE) return;

                        model.currentPlayer.playCards.remove(card);
                        model.table.playCards.add(card);
                        model.states.game = GameState.NEXT_TURN;
                    }
                });
    }
}

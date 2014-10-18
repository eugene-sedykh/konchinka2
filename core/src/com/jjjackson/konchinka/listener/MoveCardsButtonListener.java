package com.jjjackson.konchinka.listener;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.util.CardMover;
import com.jjjackson.konchinka.util.PositionCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class MoveCardsButtonListener extends ClickListener {
    protected GameModel model;
    protected TweenManager tweenManager;
    protected CardMover cardMover;
    protected List<Card> sortCards = new ArrayList<>();
    private TextButton button;

    public MoveCardsButtonListener(CardMover cardMover, GameModel model, TweenManager tweenManager, TextButton button) {
        this.cardMover = cardMover;
        this.model = model;
        this.tweenManager = tweenManager;
        this.button = button;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (this.model.fog.isVisible()) {
            moveSortedCards();
            enableOtherButtons();
        } else {
            disableCards();
            disableOtherButtons();
            this.model.fog.setVisible(true);
            this.model.fog.toFront();
            this.button.toFront();
            this.button.setTouchable(Touchable.disabled);
            moveCardsToSort();
        }
    }

    protected abstract void disableOtherButtons();

    protected abstract void enableOtherButtons();

    private void enableCards() {
        enableCards(this.model.table.playCards);
        for (User opponent : this.model.opponents) {
            enableCards(opponent.boardCards);
        }
        if (this.model.playCard != null) {
            enableCards(Collections.singletonList(this.model.playCard));
        }
    }

    private void enableCards(List<Card> cards) {
        for (Card card : cards) {
            card.setTouchable(Touchable.enabled);
        }
    }

    private void disableCards() {
        List<Card> tableCards = new ArrayList<>(this.model.table.playCards);
        tableCards.removeAll(this.model.turnCombinedCards);
        disableCards(tableCards);
        for (User opponent : this.model.opponents) {
            disableCards(opponent.boardCards);
        }
        if (this.model.playCard != null) {
            disableCards(Collections.singletonList(this.model.playCard));
        }
    }

    private void disableCards(List<Card> cards) {
        for (Card card : cards) {
            card.setTouchable(Touchable.disabled);
        }
    }

    protected void moveSortedCards() {
        if (!this.sortCards.isEmpty()) {
            Card card = this.sortCards.remove(this.sortCards.size() - 1);
            card.getListeners().clear();
            Tween tween = initBackToPlayerTween(card);
            this.model.turnCombinedCards.add(card);
            tween.start(this.tweenManager);
        } else {
            endSorting();
        }
    }

    private Tween initBackToPlayerTween(Card card) {
            card.toFront();
            return Tween.to(card, GameObject.ROTATION_XY, 0.2f).
                    target(GameConstants.BOTTOM_BOARD_X, GameConstants.BOTTOM_BOARD_Y, 90).
                    start(this.tweenManager).
                    setCallbackTriggers(TweenCallback.COMPLETE).
                    setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            if (!sortCards.isEmpty()) {
                                cardMover.changeCenterCardsPosition(sortCards, true, true);
                            }

                            moveSortedCards();
                        }
                    });
    }

    private void moveCardsToSort() {
        if (!this.model.turnCombinedCards.isEmpty()) {
            Card card = this.model.turnCombinedCards.remove(this.model.turnCombinedCards.size() - 1);
            Tween tween = initTween(card);
            this.sortCards.add(card);
            tween.start(this.tweenManager);
        } else {
            this.button.setTouchable(Touchable.enabled);
        }
    }

    private Tween initTween(final Card card) {
        card.toFront();
        Point destination = new Point();
        this.cardMover.changeCenterCardsPosition(sortCards, true, true);

        PositionCalculator.calcCenter(this.sortCards.size(), destination, true);
        card.addListener(getCardListener(card));
        return Tween.to(card, GameObject.ROTATION_XY, 0.2f).
                target(destination.x, destination.y, 0).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        moveCardsToSort();
                    }
                });
    }

    protected abstract EventListener getCardListener(Card card);

    protected void endSorting() {
        this.model.fog.setVisible(false);
        enableCards();
    }
}

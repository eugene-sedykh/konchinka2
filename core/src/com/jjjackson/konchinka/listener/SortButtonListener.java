package com.jjjackson.konchinka.listener;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.util.CardMover;
import com.jjjackson.konchinka.util.PositionCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortButtonListener extends ClickListener {
    private GameModel model;
    private TweenManager tweenManager;
    private CardMover cardMover;
    private List<Card> sortCards = new ArrayList<>();

    public SortButtonListener(GameModel model, TweenManager tweenManager, CardMover cardMover) {
        this.model = model;
        this.tweenManager = tweenManager;
        this.cardMover = cardMover;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (this.model.fog.isVisible()) {
            moveSortedCards();
        } else {
            disableCards();
            this.model.fog.setVisible(true);
            this.model.fog.toFront();
            this.model.buttons.sortButton.toFront();
            this.model.buttons.sortButton.setTouchable(Touchable.disabled);
            moveCardsToSort();
        }
    }

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
        disableCards(this.model.table.playCards);
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

    private void moveSortedCards() {
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
            this.model.buttons.sortButton.setTouchable(Touchable.enabled);
        }
    }

    private Tween initTween(final Card card) {
        card.toFront();
        Point destination = new Point();
        this.cardMover.changeCenterCardsPosition(sortCards, true, true);

        PositionCalculator.calcCenter(this.sortCards.size(), destination, true);
        card.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                moveCardToPlayer(card);
            }
        });
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

    private void moveCardToPlayer(Card card) {
        card.getListeners().clear();
        initMovement(card);
        this.model.turnCombinedCards.add(card);
        this.sortCards.remove(card);
    }

    private void initMovement(Card card) {
        card.toFront();
        Tween.to(card, GameObject.ROTATION_XY, 0.2f).
            target(GameConstants.BOTTOM_BOARD_X, GameConstants.BOTTOM_BOARD_Y, 90).
            setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                @Override
                public void onEvent(int type, BaseTween<?> source) {
                    if (!sortCards.isEmpty()) {
                        cardMover.changeCenterCardsPosition(sortCards, false, true);
                    } else {
                        endSorting();
                    }
                }
            }).start(this.tweenManager);
    }

    private void endSorting() {
        this.model.fog.setVisible(false);
        enableCards();
    }
}

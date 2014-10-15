package com.jjjackson.konchinka.listener;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
            this.model.fog.setVisible(false);
            enableCards();
            moveSortedCards();
        } else {
            disableCards();
            this.model.fog.setVisible(true);
            this.model.fog.toFront();
            this.model.buttons.sortButton.toFront();
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

    }

    private void moveCardsToSort() {
        Timeline sequence = Timeline.createSequence();
        for (int i = 0; i < this.model.turnCombinedCards.size(); i++) {
            Card card = this.model.turnCombinedCards.get(i);
            sequence.push(initTween(card, i + 1));
        }
        sequence.start(this.tweenManager).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
//                        model.player.boardCards.addAll(combinedCards);
//                        removeListeners(combinedCards);
//                        model.turnCombinedCards.addAll(combinedCards);
//                        combinedCards.clear();
//                        model.buttons.sortButton.setVisible(true);
//                        centerTableCards();
                    }
                });
    }

    private Tween initTween(final Card card, int i) {
        card.toFront();
        Point destination = new Point();
        PositionCalculator.calcCenter(i, destination, true);
        return Tween.to(card, GameObject.ROTATION_XY, 0.2f).
                target(destination.x, destination.y, 0).
                start(this.tweenManager).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if (type == COMPLETE) {
                            card.addListener(new ClickListener() {
                                @Override
                                public void clicked(InputEvent event, float x, float y) {
                                    Gdx.app.log("blabla", "blablabla");
                                }
                            });

                        } else if (type == START){
                            cardMover.changeCenterCardsPosition(sortCards, true, true);
                            sortCards.add(card);
                        }
                    }
                });
    }
}

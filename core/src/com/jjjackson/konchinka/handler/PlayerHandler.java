package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.util.PlayerUtil;
import com.jjjackson.konchinka.util.PositionCalculator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayerHandler extends GameObjectHandler {

    private int playCardValue;
    private List<Card> combinedCards = new ArrayList<>();

    public PlayerHandler(GameModel model, TweenManager tweenManager) {
        super(model, tweenManager);
    }

    @Override
    public void handle() {
        switch (this.model.states.turn) {
            case NONE:
                addPlayCardListeners(this.model.player.playCards);
                this.model.states.turn = TurnState.WAIT;
                break;
        }
    }

    private void addPlayCardListeners(final List<Card> playCards) {
        for (Card playCard : playCards) {
            playCard.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    initCardMovement((Card)event.getTarget());
                    removeListeners(playCards);
                }
            });
        }
    }

    private void initCardMovement(final Card card) {
        Tween.to(card, GameObject.POSITION_XY, 0.2f).
                target(GameConstants.PLAY_CARD_X, GameConstants.PLAY_CARD_Y).
                start(this.tweenManager).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if (type != COMPLETE) return;

                        if (!canCombine(card)) {
                            moveCardToTable(card);
                            return;
                        }

                        if (card.value == GameConstants.JACK_VALUE) {
                            addDoubleClickListener(model.table.playCards);
                            List<Card> opponentCards = getCardsHeap();
                            opponentCards.removeAll(model.table.playCards);
                            addSingleClickListener(opponentCards);
                        } else {
                            addSingleClickListener(getCardsHeap());
                        }

                        playCardValue = card.value;
                    }
                });
    }

    private void addDoubleClickListener(List<Card> cards) {
        for (Card card : cards) {
            card.addListener(new ClickListener() {

                private DoubleClickRunnable doubleClickRunnable;

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (getTapCount() == 1) {
                        this.doubleClickRunnable = new DoubleClickRunnable();
                        new Thread(this.doubleClickRunnable).start();
                    } else if (getTapCount() == 2) {
                        if (this.doubleClickRunnable != null) {
                            this.doubleClickRunnable.setCanceled(true);
                        }
                    }
                }
            });
        }
    }

    private boolean canCombine(Card card) {
        return cardCombinator.isCombinationPresent(getCardsHeap(), card.value) ||
                (card.value == GameConstants.JACK_VALUE && !this.model.table.playCards.isEmpty());
    }

    private void addSingleClickListener(List<Card> cards) {
        for (final Card card : cards) {
            card.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    int newSum = getCombinedCardsSum() + card.value;
                    if (card.isMarked) {
                        unmark(card);
                    } else {
                        if (playCardValue > newSum) {
                            mark(card);
                        } else if (playCardValue == newSum) {
                            mark(card);
                            takeCombinedCards();
                        } else {
                            unmark(combinedCards);
                        }
                    }
                }
            });
        }
    }

    private void takeCombinedCards() {
        Timeline sequence = Timeline.createSequence();
        Iterator<Card> iterator = this.combinedCards.iterator();
        while (iterator.hasNext()) {
            Card card = iterator.next();
            card.isMarked = false;
            sequence.push(initTween(card));
            iterator.remove();
        }
        sequence.start(this.tweenManager);
    }

    private Tween initTween(Card card) {
        return Tween.to(card, GameObject.POSITION_XY, 0.2f).
                target(GameConstants.BOTTOM_BOARD_X, GameConstants.BOTTOM_BOARD_Y).
                start(this.tweenManager).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if (type != COMPLETE) return;


                    }
                });
    }

    private void unmark(List<Card> combinedCards) {
        Iterator<Card> iterator = combinedCards.iterator();
        while (iterator.hasNext()) {
            Card card = iterator.next();
            card.isMarked = false;
            iterator.remove();
        }
    }

    private void mark(Card card) {
        card.isMarked = true;
        this.combinedCards.add(card);
    }

    private void unmark(Card card) {
        card.isMarked = false;
        this.combinedCards.remove(card);
    }

    private int getCombinedCardsSum() {
        int res = 0;

        for (Card combinedCard : this.combinedCards) {
            res += combinedCard.value;
        }

        return res;
    }

    private void moveCardToTable(Actor card) {
        this.cardMover.changeCenterCardsPosition(this.model.table.playCards, false);
        Point destination = new Point();
        PositionCalculator.calcCenter(this.model.table.playCards.size(), destination);
        Tween.to(card, GameObject.POSITION_XY, 0.2f).
                target(destination.x, destination.y).
                start(this.tweenManager).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if (type != COMPLETE) return;

                        PlayerUtil.switchPlayer(model);
                    }
                });
    }

    private List<Card> getCardsHeap() {
        List<Card> cards = new ArrayList<>();

        cards.addAll(model.table.playCards);
        for (User user : model.opponents) {
            if (user.boardCards.size() == 0) continue;
            cards.add(user.boardCards.get(user.boardCards.size() - 1));
        }

        return cards;
    }

    private void removeListeners(List<Card> playCards) {
        for (Card playCard : playCards) {
            playCard.getListeners().clear();
        }
    }

    private class DoubleClickRunnable implements Runnable {

        private volatile boolean isCanceled;

        @Override
        public void run() {
            try {
                Thread.sleep(300);
                if (!this.isCanceled) {
                    Gdx.app.log("JJJ", "single click");
                } else {
                    Gdx.app.log("JJJ", "double click");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void setCanceled(boolean canceled) {
            this.isCanceled = canceled;
        }
    }
}

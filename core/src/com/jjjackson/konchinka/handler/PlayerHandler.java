package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.listener.EndButtonListener;
import com.jjjackson.konchinka.listener.SortButtonListener;
import com.jjjackson.konchinka.listener.TrickButtonListener;
import com.jjjackson.konchinka.util.PlayerUtil;
import com.jjjackson.konchinka.util.PositionCalculator;

import java.util.ArrayList;
import java.util.Collections;
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
                initListeners();
                this.model.states.turn = TurnState.WAIT;
                break;
        }
    }

    private void initListeners() {
        addPlayCardListeners(this.model.player.playCards);
        model.buttons.sortButton.addListener(new SortButtonListener(this.model, this.tweenManager,
                this.cardMover, this.model.buttons.sortButton));
        model.buttons.trickButton.addListener(new TrickButtonListener(this.model, this.tweenManager,
                this.cardMover, this.model.buttons.trickButton));
        model.buttons.endButton.addListener(new EndButtonListener(this.model, this.tweenManager));
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
        card.toFront();
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
                        addPlayCardListener(card);
                        model.playCard = card;
                        model.player.playCards.remove(card);
                        playCardValue = card.value;
                    }
                });
    }

    private void addPlayCardListener(final Card card) {
        card.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                card.toFront();
                Tween.to(card, GameObject.ROTATION_XY, 0.2f).
                        target(GameConstants.BOTTOM_BOARD_X, GameConstants.BOTTOM_BOARD_Y, 90).
                        start(tweenManager).
                        setCallback(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                if (type != COMPLETE) return;
                                model.turnCombinedCards.add(card);
                                card.getListeners().clear();
                                model.playCard = null;
                            }
                        });
            }
        });

    }

    private void addDoubleClickListener(List<Card> cards) {
        for (final Card card : cards) {
            card.addListener(new ClickListener() {

                private DoubleClickRunnable doubleClickRunnable;

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (getTapCount() == 1) {
                        this.doubleClickRunnable = new DoubleClickRunnable(card);
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
                    processSingleClick(card);
                }
            });
        }
    }

    private void processSingleClick(Card card) {
        int newSum = getCombinedCardsSum() + card.value;
        if (card.isMarked) {
            unmark(card);
        } else {
            if (this.playCardValue > newSum) {
                mark(card);
            } else if (this.playCardValue == newSum) {
                mark(card);
                takeCombinedCards();

            } else {
                unmark(this.combinedCards);
            }
        }
    }

    private void takeCombinedCards() {
        Timeline sequence = Timeline.createSequence();
        for (Card card : this.combinedCards) {
            card.isMarked = false;
            sequence.push(initTween(card));
        }
        sequence.start(this.tweenManager).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        removeListeners(combinedCards);
                        model.turnCombinedCards.addAll(combinedCards);
                        combinedCards.clear();
                        model.buttons.sortButton.setVisible(true);
                        centerTableCards();
                        enableEndButtons();
                    }
                });
    }

    private void enableEndButtons() {
        if (this.playCardValue != GameConstants.JACK_VALUE) {
            if (shouldShowTrickButton()) {
                model.buttons.trickButton.setVisible(true);
                model.buttons.trickButton.toFront();
            } else {
                model.buttons.endButton.setVisible(true);
                model.buttons.endButton.toFront();
            }
        } else if (shouldShowTrickButton()) {
            model.buttons.trickButton.setVisible(true);
            model.buttons.trickButton.toFront();
        }
    }

    private boolean shouldShowTrickButton() {
        return this.model.turnCombinedCards.containsAll(this.model.table.playCards) &&
                !this.model.table.playCards.isEmpty() && !this.model.isTrickTaken;
    }

    private Tween initTween(Card card) {
        card.toFront();
        return Tween.to(card, GameObject.ROTATION_XY, 0.2f).
                target(GameConstants.BOTTOM_BOARD_X, GameConstants.BOTTOM_BOARD_Y, 90).
                start(this.tweenManager);
    }

    private void centerTableCards() {
        List<Card> cards = new ArrayList<>(this.model.table.playCards);
        cards.removeAll(this.model.turnCombinedCards);
        cardMover.changeCenterCardsPosition(cards, false);
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
        card.toFront();
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

        private static final int DELAY = 200;
        private volatile boolean isCanceled;
        private Card card;

        public DoubleClickRunnable(Card card) {
            this.card = card;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(DELAY);
                if (!this.isCanceled) {
                    processSingleClick(this.card);
                } else {
                    processDoubleClick(this.card);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void setCanceled(boolean canceled) {
            this.isCanceled = canceled;
        }
    }

    private void processDoubleClick(final Card card) {
        card.isMarked = false;
        card.toFront();
        Tween.to(card, GameObject.ROTATION_XY, 0.2f).
                target(GameConstants.BOTTOM_BOARD_X, GameConstants.BOTTOM_BOARD_Y, 90).
                start(this.tweenManager).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if (type != COMPLETE) return;

                        combinedCards.remove(card);
                        model.turnCombinedCards.add(card);
                        removeListeners(Collections.singletonList(card));
                        model.buttons.sortButton.setVisible(true);
                        centerTableCards();
                        enableEndButtons();
                    }
                });
    }
}

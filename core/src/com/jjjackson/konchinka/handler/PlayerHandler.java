package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.User;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.domain.state.TurnState;
import com.jjjackson.konchinka.listener.EndButtonListener;
import com.jjjackson.konchinka.listener.SortButtonListener;
import com.jjjackson.konchinka.listener.TrickButtonListener;
import com.jjjackson.konchinka.objectmover.ObjectMover;
import com.jjjackson.konchinka.objectmover.TweenInfo;
import com.jjjackson.konchinka.util.ActorHelper;
import com.jjjackson.konchinka.util.PlayerUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class PlayerHandler extends GameObjectHandler {

    private int playCardValue;
    private List<Card> combinedCards = new ArrayList<>();

    public PlayerHandler(GameModel model, ObjectMover tweenManager) {
        super(model, tweenManager);
    }

    @Override
    public void handle() {
        switch (model.states.turn) {
            case INIT_BUTTONS:
                model.buttons.sortButton.addListener(new SortButtonListener(model, objectMover,
                        model.buttons.sortButton));
                model.buttons.trickButton.addListener(new TrickButtonListener(model, objectMover,
                        model.buttons.trickButton));
                model.buttons.endButton.addListener(new EndButtonListener(model, objectMover,
                        combinedCards));
                model.states.turn = TurnState.INIT_PLAY_CARDS_LISTENERS;
                Gdx.app.log(PlayerHandler.class.getSimpleName(), model.states.turn.toString());
                break;
            case INIT_PLAY_CARDS_LISTENERS:
                addPlayCardListeners(model.player.playCards);
                model.states.turn = TurnState.ENABLE_CARDS_AND_PLAYER;
                Gdx.app.log(PlayerHandler.class.getSimpleName(), model.states.turn.toString());
                break;
            case ENABLE_CARDS_AND_PLAYER:
                combinedCards.clear();
                model.turnCombinedCards.clear();
                ActorHelper.enable(model.player.playCards);
                PlayerUtil.enablePlayer(model.currentPlayer);
                model.states.turn = TurnState.WAIT;
                Gdx.app.log(PlayerHandler.class.getSimpleName(), model.states.turn.toString());
                break;
        }
    }

    private void addPlayCardListeners(final List<Card> playCards) {
        for (final Card playCard : playCards) {
            playCard.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Card card = (Card) event.getTarget();
                    playCards.remove(card);
                    removeListeners(Collections.singletonList(card));
                    initCardMovement(card);
                    ActorHelper.disable(playCards);
                }
            });
        }
    }

    private void initCardMovement(final Card card) {
        card.toFront();

        TweenInfo tweenInfo = card.tweenInfo;
        tweenInfo.x = GameConstants.PLAY_CARD_X;
        tweenInfo.y = GameConstants.PLAY_CARD_Y;
        tweenInfo.angle = GameConstants.ANGLE_VERTICAL;
        tweenInfo.speed = GameConstants.CARD_SPEED;
        tweenInfo.tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                if (!canCombine(card)) {
                    moveCardToTable(card, new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            model.table.playCards.add(card);
                            if (PlayerUtil.wasLastTurn(model)) {
                                takeTrickAndRemainingCards(model.table.playCards.remove(0));
                            } else {
                                setNextState();
                            }
                        }
                    });
                    return;
                }

                addSingleClickListener(getTouchableCards());
                if (card.isJack() || PlayerUtil.wasLastTurn(model)) {
                    removeListeners(model.table.playCards);
                    addDoubleClickListener(model.table.playCards);
                }
                addPlayCardListener(card);

                model.playCard = card;
                playCardValue = card.value;
            }
        };
        objectMover.move(card);
    }

    private void takeTrickAndRemainingCards(final Card trick) {
        TweenInfo tweenInfo = trick.tweenInfo;
        tweenInfo.x = GameConstants.TRICK_BOTTOM_X;
        tweenInfo.y = GameConstants.TRICK_BOTTOM_Y;
        tweenInfo.angle = GameConstants.ANGLE_VERTICAL;
        tweenInfo.speed = GameConstants.CARD_SPEED;
        tweenInfo.tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                trick.showBack();
                ((User)model.currentPlayer).tricks.add(trick);
                model.table.playCards.remove(trick);

                if (model.table.playCards.isEmpty()) {
                    setNextState();
                } else {
                    ActorHelper.takeTableCards(model, objectMover, new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            setNextState();
                        }
                    });
                }
            }
        };

        objectMover.move(trick);
        model.states.turn = TurnState.WAIT;
    }

    private void setNextState() {
        model.states.game = GameState.NEXT_TURN;
        model.states.turn = model.player.playCards.isEmpty() ? TurnState.INIT_PLAY_CARDS_LISTENERS :
                TurnState.ENABLE_CARDS_AND_PLAYER;
    }

    private void addPlayCardListener(final Card card) {
        card.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                card.toFront();
                removeListeners(Collections.singletonList(card));
                model.turnCombinedCards.add(card);
                model.playCard = null;

                TweenInfo tweenInfo = card.tweenInfo;
                tweenInfo.x = GameConstants.BOARD_BOTTOM_X;
                tweenInfo.y = GameConstants.BOARD_BOTTOM_Y;
                tweenInfo.angle = GameConstants.ANGLE_HORIZONTAL;
                tweenInfo.speed = GameConstants.CARD_SPEED;
                tweenInfo.tweenCallback = null;

                objectMover.move(card);
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
                        doubleClickRunnable = new DoubleClickRunnable(card);
                        new Thread(doubleClickRunnable).start();
                    } else if (getTapCount() == 2) {
                        if (doubleClickRunnable != null) {
                            doubleClickRunnable.setCanceled(true);
                        }
                    }
                }
            });
        }
    }

    private boolean canCombine(Card card) {
        return cardCombinator.isCombinationPresent(getCardsHeap(), card.value) ||
                (card.value == GameConstants.JACK_VALUE && !model.table.playCards.isEmpty());
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
        if (card.isMarked()) {
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

    private void takeCombinedCards() {
        for (Card card : combinedCards) {
            card.unmark();
            initTween(card);
        }

        objectMover.move(combinedCards, true, new TweenCallback() {
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
        if (playCardValue != GameConstants.JACK_VALUE ) {
            if (shouldShowTrickButton()) {
                showTrickButton();
            } else {
                showEndButton();
            }
        } else if (shouldShowTrickButton()) {
            showTrickButton();
        } else if (model.table.playCards.isEmpty()) {
            showEndButton();
        }
    }

    private void showTrickButton() {
        model.buttons.trickButton.setVisible(true);
        model.buttons.trickButton.toFront();
    }

    private void showEndButton() {
        model.buttons.endButton.setVisible(true);
        model.buttons.endButton.toFront();
    }

    private boolean shouldShowTrickButton() {
        return (model.turnCombinedCards.containsAll(model.table.playCards) &&
                !model.table.playCards.isEmpty() && !model.isTrickTaken) ||
                (PlayerUtil.wasLastTurn(model) && !model.isTrickTaken);
    }

    private void initTween(Card card) {
        card.toFront();

        TweenInfo tweenInfo = card.tweenInfo;
        tweenInfo.x = GameConstants.BOARD_BOTTOM_X;
        tweenInfo.y = GameConstants.BOARD_BOTTOM_Y;
        tweenInfo.angle = GameConstants.ANGLE_HORIZONTAL;
        tweenInfo.speed = GameConstants.CARD_SPEED;
        tweenInfo.tweenCallback = null;
    }

    private void centerTableCards() {
        List<Card> cards = new ArrayList<>(model.table.playCards);
        cards.removeAll(model.turnCombinedCards);
        objectMover.changeCenterCardsPosition(cards, false);
    }

    private void unmark(List<Card> combinedCards) {
        Iterator<Card> iterator = combinedCards.iterator();
        while (iterator.hasNext()) {
            Card card = iterator.next();
            card.unmark();
            iterator.remove();
        }
    }

    private void mark(Card card) {
        card.mark();
        combinedCards.add(card);
    }

    private void unmark(Card card) {
        card.unmark();
        combinedCards.remove(card);
    }

    private int getCombinedCardsSum() {
        int res = 0;

        for (Card combinedCard : combinedCards) {
            res += combinedCard.value;
        }

        return res;
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

    private List<Card> getTouchableCards() {
        List<Card> cards = new ArrayList<>();

        cards.addAll(model.table.playCards);
        for (User user : model.opponents) {
            cards.addAll(user.boardCards);
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
                if (!isCanceled) {
                    processSingleClick(card);
                } else {
                    processDoubleClick(card);
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
        card.unmark();
        card.toFront();

        TweenInfo tweenInfo = card.tweenInfo;
        tweenInfo.x = GameConstants.BOARD_BOTTOM_X;
        tweenInfo.y = GameConstants.BOARD_BOTTOM_Y;
        tweenInfo.angle = GameConstants.ANGLE_HORIZONTAL;
        tweenInfo.speed = GameConstants.CARD_SPEED;
        tweenInfo.tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                combinedCards.remove(card);
                model.turnCombinedCards.add(card);
                removeListeners(Collections.singletonList(card));
                model.buttons.sortButton.setVisible(true);
                centerTableCards();
                enableEndButtons();
            }
        };
        objectMover.move(card);
    }
}

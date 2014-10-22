package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.graphics.Color;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.domain.state.CpuTurn;
import com.jjjackson.konchinka.util.PositionCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OpponentHandler extends GameObjectHandler {

    private Card playCard;
    private List<Card> combinedCards;
    private List<Card> turnCombinedCards = new ArrayList<>();
    private List<Card> initialTable;
    private List<Card> buffer = new ArrayList<>();

    public OpponentHandler(GameModel model, TweenManager tweenManager) {
        super(model, tweenManager);
    }

    @Override
    public void handle() {
        switch (this.model.states.cpuTurn) {
            case NONE:
                this.model.states.cpuTurn = CpuTurn.CHOOSE_PLAY_CARD;
                break;
            case CHOOSE_PLAY_CARD:
                this.initialTable = new ArrayList<>(this.model.table.playCards);
                choosePlayCard();
                initPlayCardMovement(this.playCard);
                this.model.states.cpuTurn = CpuTurn.WAIT;
                break;
            case WAIT:
                break;
        }
    }

    private void initPlayCardMovement(final Card card) {
        card.toFront();
        card.setDrawable(this.model.skin, card.face);
        Tween.to(card, GameObject.POSITION_XY, 0.2f).
                target(GameConstants.PLAY_CARD_X, GameConstants.PLAY_CARD_Y).
                start(this.tweenManager).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if (type != COMPLETE) return;

                        if (combinedCards.isEmpty()) {
                            moveCardToTable(card);
                        } else {
                            markCombinedCards();
                        }
                    }
                });
    }

    private void markCombinedCards() {
        Timeline sequence = Timeline.createSequence();
        for (Card card : this.combinedCards) {
            sequence.push(initMarking(card));
        }
        sequence.start(this.tweenManager).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        takeCombinedCards();
                    }
                });
    }

    private Tween initMarking(Card card) {
        return Tween.to(card, GameObject.COLOR, 0.2f).
                target(Color.GRAY.r, Color.GRAY.g, Color.GRAY.b, 0.8f).
                delay(0.5f);
    }

    private void takeCombinedCards() {
        Timeline sequence = Timeline.createSequence();
        CardHolder user = getCurrentPlayer();
        Point destination = PositionCalculator.calcBoard(user.cardPosition);
        int degree = PositionCalculator.calcRotation(user.cardPosition);
        for (Card card : this.combinedCards) {
            sequence.push(initTake(card, destination, degree));
        }
        sequence.start(this.tweenManager).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        cardMover.changeCenterCardsPosition(model.table.playCards, false, false);
                        findCombinations();
                    }
                });
    }

    private void findCombinations() {
        CardCombination combination = this.cardCombinator.calculateAndChooseCombination(getPlayCardHeap(),
                Collections.singletonList(this.playCard), this.model.table);

        if (combination.combination.isEmpty()) {
            takePlayCard();
        } else {
            this.combinedCards = combination.combination;
            markCombinedCards();
        }
    }

    private void takePlayCard() {
        CardHolder user = getCurrentPlayer();
        Point destination = PositionCalculator.calcBoard(user.cardPosition);
        int degree = PositionCalculator.calcRotation(user.cardPosition);
        this.playCard.toFront();
        Tween.to(this.playCard, GameObject.ROTATION_XY, 0.2f).
                target(destination.x, destination.y, degree).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        turnCombinedCards.add(playCard);
                        endTurn();
                    }
                }).
                delay(0.3f);
    }

    private void endTurn() {
        List<Card> sortedCards = sort(this.turnCombinedCards);

        if (needToSort(sortedCards, this.turnCombinedCards)) {
            model.fog.setVisible(true);
            model.fog.toFront();
            moveCardsToSort(sortedCards, this.turnCombinedCards);
        } else {
            takeTrickIfExists();
        }
    }

    private boolean needToSort(List<Card> sortedCards, List<Card> turnCombinedCards) {
        for (int i = 0; i < sortedCards.size(); i++) {
            if (sortedCards.get(i) != turnCombinedCards.get(i)) {
                return true;
            }
        }
        return false;
    }

    private void moveCardsToSort(List<Card> sortedCards, List<Card> turnCombinedCards) {
        if (!turnCombinedCards.isEmpty()) {
            Card card = turnCombinedCards.remove(turnCombinedCards.size() - 1);
            Tween tween = initTween(card, sortedCards);
            this.buffer.add(card);
            tween.start(this.tweenManager);
        } else {
            moveCardsToPlayer(sortedCards);
        }
    }

    private Tween initTween(final Card card, final List<Card> sortedCards) {
        card.toFront();
        Point destination = new Point();
        this.cardMover.changeCenterCardsPosition(this.buffer, true, true);

        PositionCalculator.calcCenter(this.buffer.size(), destination, true);
        return Tween.to(card, GameObject.ROTATION_XY, 0.2f).
                target(destination.x, destination.y, 0).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        moveCardsToSort(sortedCards, turnCombinedCards);
                    }
                });
    }

    private void moveCardsToPlayer(List<Card> sortedCards) {
        if (!sortedCards.isEmpty()) {
            Card card = sortedCards.remove(sortedCards.size() - 1);
            Tween tween = initBackTween(card, sortedCards);
            this.buffer.remove(card);
            this.turnCombinedCards.add(card);
            tween.start(this.tweenManager);
        } else {
            takeTrickIfExists();
        }
    }

    private void takeTrickIfExists() {

    }

    private Tween initBackTween(Card card, final List<Card> sortedCards) {
        card.toFront();
        return Tween.to(card, GameObject.ROTATION_XY, 0.2f).
                target(GameConstants.BOTTOM_BOARD_X, GameConstants.BOTTOM_BOARD_Y, 90).
                start(this.tweenManager).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if (!buffer.isEmpty()) {
                            cardMover.changeCenterCardsPosition(buffer, true, true);
                        }

                        moveCardsToPlayer(sortedCards);
                    }
                });
    }

    private ArrayList<Card> sort(List<Card> turnCombinedCards) {
        ArrayList<Card> cards = new ArrayList<>(turnCombinedCards);
        Collections.sort(cards, new Comparator<Card>() {
            @Override
            public int compare(Card lhs, Card rhs) {
                if (GameConstants.VALUABLE_CARDS.contains(lhs.face) && GameConstants.VALUABLE_CARDS.contains(rhs.face)) {
                    if (lhs.value == rhs.value) {
                        return 0;
                    }
                    return lhs.value > rhs.value ? 1 : -1;
                }

                if (GameConstants.VALUABLE_CARDS.contains(lhs.face) && !GameConstants.VALUABLE_CARDS.contains(rhs.face)) {
                    return -1;
                }
                if (!GameConstants.VALUABLE_CARDS.contains(lhs.face) && GameConstants.VALUABLE_CARDS.contains(rhs.face)) {
                    return 1;
                }

                if (lhs.value == GameConstants.JACK_VALUE) return 1;

                if (lhs.cardSuit == CardSuit.CLUBS && rhs.cardSuit != CardSuit.CLUBS) {
                    return -1;
                }
                if (lhs.cardSuit != CardSuit.CLUBS && rhs.cardSuit == CardSuit.CLUBS) {
                    return 1;
                }
                if (lhs.cardSuit == CardSuit.CLUBS) {
                    return lhs.value > rhs.value ? 1 : -1;
                }

                return lhs.value > rhs.value ? 1 : -1;
            }
        });
        return cards;
    }

    private Tween initTake(final Card card, Point destination, int degree) {
        card.unmark();
        card.toFront();
        return Tween.to(card, GameObject.ROTATION_XY, 0.2f).
                target(destination.x, destination.y, degree).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        turnCombinedCards.add(card);
                        combinedCards.remove(card);
                        model.table.playCards.remove(card);
                        for (CardHolder cardHolder : model.cardHolders) {
                            ((User) cardHolder).boardCards.remove(card);
                        }
                    }
                }).
                delay(0.3f);
    }

    private void choosePlayCard() {
        List<Card> playCardHeap = getPlayCardHeap();
        CardCombination bestCombination = this.cardCombinator.calculateAndChooseCombination(playCardHeap,
                this.model.currentPlayer.playCards, this.model.table);
        this.playCard = bestCombination.card;
        this.combinedCards = bestCombination.combination;
        this.model.currentPlayer.playCards.remove(bestCombination.card);
    }

    private List<Card> getPlayCardHeap() {
        List<Card> cards = new ArrayList<>();

        for (CardHolder cardHolder : this.model.cardHolders) {
            if (cardHolder.isCurrent) continue;

            User user = (User) cardHolder;
            if (user.boardCards.isEmpty()) continue;
            cards.add(user.boardCards.get(user.boardCards.size() - 1));
        }
        cards.addAll(this.model.table.playCards);

        return cards;
    }


}

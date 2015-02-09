package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.domain.state.CpuTurn;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.util.PlayerUtil;
import com.jjjackson.konchinka.util.PositionCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OpponentHandler extends GameObjectHandler {

    private Card playCard;
    private List<Card> combinedCards = Collections.synchronizedList(new ArrayList<Card>());
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
                PlayerUtil.enablePlayer(this.model.currentPlayer);
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
        Tween.to(card, GameObject.POSITION_XY, GameConstants.CARD_SPEED).
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
                        model.stage.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                takeCombinedCards();
                            }
                        });
                    }
                });
    }

    private Tween initMarking(Card card) {
        return Tween.to(card, GameObject.COLOR, GameConstants.CARD_SPEED).
                target(Color.GRAY.r, Color.GRAY.g, Color.GRAY.b).
                delay(0.5f);
    }

    private void takeCombinedCards() {
        removeStageListeners();
        Timeline sequence = Timeline.createSequence();
        CardHolder user = getCurrentPlayer();
        Point destination = PositionCalculator.calcBoard(user.cardPosition, this.model.opponents.size());
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

    private void removeStageListeners() {
        this.model.stage.getRoot().getListeners().clear();
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
        Point destination = PositionCalculator.calcBoard(user.cardPosition, this.model.opponents.size());
        int degree = PositionCalculator.calcRotation(user.cardPosition);
        this.playCard.toFront();
        Tween.to(this.playCard, GameObject.ROTATION_XY, GameConstants.CARD_SPEED).
                target(destination.x, destination.y, degree).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        turnCombinedCards.add(playCard);
                        sortAndTrick();
                    }
                }).
                delay(0.3f).
                start(this.tweenManager);
    }

    private void sortAndTrick() {
        List<Card> sortedCards = sort(this.turnCombinedCards);

        if (needToSort(sortedCards, this.turnCombinedCards) || needTakeTrick()) {
            model.fog.setVisible(true);
            model.fog.toFront();
            moveCardsToSort(sortedCards, this.turnCombinedCards);
        } else {
            endTurn();
        }
    }

    private void endTurn() {
        this.model.fog.setVisible(false);
        this.model.fog.toBack();
        this.model.states.game = GameState.NEXT_TURN;
        this.model.states.cpuTurn = CpuTurn.NONE;
        ((User) this.model.currentPlayer).boardCards.addAll(this.turnCombinedCards);
        this.playCard = null;
        this.turnCombinedCards.clear();
    }

    private boolean needToSort(List<Card> sortedCards, List<Card> turnCombinedCards) {
        for (int i = 0; i < sortedCards.size(); i++) {
            if (sortedCards.get(i) != turnCombinedCards.get(i)) {
                return true;
            }
        }
        return false;
    }

    private void moveCardsToSort(final List<Card> sortedCards, List<Card> turnCombinedCards) {
        if (!turnCombinedCards.isEmpty()) {
            Card card = turnCombinedCards.remove(turnCombinedCards.size() - 1);
            this.cardMover.showOnFogLayer(card);
            Tween tween = initTween(card, sortedCards);
            this.buffer.add(card);
            tween.start(this.tweenManager);
        } else {
            if (this.buffer.containsAll(this.initialTable) && !this.initialTable.isEmpty() || isLastTurn()) {
                sortedCards.get(0).mark();
                this.model.stage.getRoot().addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        initTrickTaking(sortedCards);
                    }
                });
            } else {
                moveCardsToPlayer(sortedCards);
            }
        }
    }

    private void initTrickTaking(List<Card> sortedCards) {
        Card trick = sortedCards.remove(0);
        trick.unmark();
        this.buffer.remove(trick);
        ((User)this.model.currentPlayer).tricks.add(trick);
        takeTrick(trick, this.model.currentPlayer.cardPosition, sortedCards);
    }

    private boolean needTakeTrick() {
        return this.turnCombinedCards.containsAll(this.initialTable) && !this.initialTable.isEmpty() || isLastTurn();
    }

    private void takeTrick(final Card trick, CardPosition cardPosition, final List<Card> sortedCards) {
        this.cardMover.showOnCardsLayer(trick);
        trick.toFront();
        Point destination = PositionCalculator.calcTrick(cardPosition, this.model.opponents.size());
        Tween.to(trick, GameObject.ROTATION_XY, GameConstants.CARD_SPEED).
                target(destination.x, destination.y, 0).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        trick.showBack();
                        moveCardsToPlayer(sortedCards);
                        removeStageListeners();
                    }
                }).
                start(this.tweenManager);
    }

    private Tween initTween(final Card card, final List<Card> sortedCards) {
        card.toFront();
        Point destination = new Point();
        this.cardMover.changeCenterCardsPosition(this.buffer, true, true);

        PositionCalculator.calcCenter(this.buffer.size(), destination, true);
        return Tween.to(card, GameObject.ROTATION_XY, GameConstants.CARD_SPEED).
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
            Card card = sortedCards.remove(0);
            Tween tween = initBackTween(card, sortedCards);
            this.buffer.remove(card);
            this.turnCombinedCards.add(card);
            tween.start(this.tweenManager);
        } else {
            endTurn();
        }
    }

    private Tween initBackTween(Card card, final List<Card> sortedCards) {
        this.cardMover.showOnCardsLayer(card);
        card.toFront();
        Point destination = PositionCalculator.calcBoard(this.model.currentPlayer.cardPosition, this.model.opponents.size());
        int degree = PositionCalculator.calcRotation(this.model.currentPlayer.cardPosition);
        return Tween.to(card, GameObject.ROTATION_XY, GameConstants.CARD_SPEED).
                target(destination.x, destination.y, degree).
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
        Collections.sort(cards, new CardsSorter());
        return cards;
    }

    private Tween initTake(final Card card, Point destination, int degree) {
        card.unmark();
        card.toFront();
        return Tween.to(card, GameObject.ROTATION_XY, GameConstants.CARD_SPEED).
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

package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.domain.state.CpuTurn;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.objectmover.ObjectMover;
import com.jjjackson.konchinka.objectmover.TweenInfo;
import com.jjjackson.konchinka.util.ActorHelper;
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

    public OpponentHandler(GameModel model, ObjectMover tweenManager) {
        super(model, tweenManager);
    }

    @Override
    public void handle() {
        switch (model.states.cpuTurn) {
            case NONE:
                PlayerUtil.enablePlayer(model.currentPlayer);
                model.states.cpuTurn = CpuTurn.CHOOSE_PLAY_CARD;
                break;
            case CHOOSE_PLAY_CARD:
                initialTable = new ArrayList<>(model.table.playCards);
                choosePlayCard();
                initPlayCardMovement(playCard);
                model.states.cpuTurn = CpuTurn.WAIT;
                break;
            case WAIT:
                break;
        }
    }

    private void choosePlayCard() {
        List<Card> playCardHeap = getPlayCardHeap();
        CardCombination bestCombination = cardCombinator.calculateAndChooseCombination(playCardHeap,
                model.currentPlayer.playCards, model.table);
        playCard = bestCombination.card;
        combinedCards = bestCombination.combination;
        model.currentPlayer.playCards.remove(bestCombination.card);
    }

    private List<Card> getPlayCardHeap() {
        List<Card> cards = new ArrayList<>();

        for (CardHolder cardHolder : model.cardHolders) {
            if (cardHolder == model.currentPlayer) continue;

            User user = (User) cardHolder;
            if (user.boardCards.isEmpty()) continue;
            cards.add(user.boardCards.get(user.boardCards.size() - 1));
        }
        cards.addAll(model.table.playCards);

        return cards;
    }

    private void initPlayCardMovement(final Card card) {
        card.toFront();
        card.showFace();

        card.tweenInfo.x = GameConstants.PLAY_CARD_X;
        card.tweenInfo.y = GameConstants.PLAY_CARD_Y;
        card.tweenInfo.angle = card.getRotation();
        card.tweenInfo.tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                if (combinedCards.isEmpty()) {
                    moveCardToTable(card, new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            model.currentPlayer.playCards.remove(card);
                            model.table.playCards.add(card);
                            if (PlayerUtil.wasLastTurn(model)) {
                                takeLastTrick(model.table.playCards.remove(0));
                            } else {
                                setNextState();
                            }
                        }
                    });
                } else {
                    markCombinedCards();
                }
            }
        };
        objectMover.move(card);
    }

    private void takeLastTrick(final Card card) {
        card.toFront();
        Point destination = PositionCalculator.calcTrick(model.currentPlayer.cardPosition, model.opponents.size);

        card.tweenInfo.x = destination.x;
        card.tweenInfo.y = destination.y;
        card.tweenInfo.angle = GameConstants.ANGLE_VERTICAL;
        card.tweenInfo.tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                card.showBack();
                ((User) model.currentPlayer).tricks.add(card);
                ActorHelper.takeTableCards(model, objectMover, new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        setNextState();
                    }
                });
                removeStageListeners();
            }
        };

        objectMover.move(card);
    }

    private void markCombinedCards() {
        objectMover.mark(combinedCards, new TweenCallback() {
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

    private void takeCombinedCards() {
        removeStageListeners();
        CardHolder user = model.currentPlayer;
        Point destination = PositionCalculator.calcBoard(user.cardPosition, model.opponents.size);
        int degree = PositionCalculator.calcRotation(user.cardPosition);
        TweenCallback tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                Card card = (Card) source.getUserData();
                turnCombinedCards.add(card);
                combinedCards.remove(card);
                model.table.playCards.remove(card);
                for (CardHolder cardHolder : model.cardHolders) {
                    ((User) cardHolder).boardCards.remove(card);
                }
            }
        };

        for (Card card : combinedCards) {
            card.unmark();

            TweenInfo tweenInfo = card.tweenInfo;
            tweenInfo.x = destination.x;
            tweenInfo.y = destination.y;
            tweenInfo.angle = degree;
            tweenInfo.delay = 0.3f;
            tweenInfo.userData = card;
            tweenInfo.tweenCallback = tweenCallback;
        }

        objectMover.move(combinedCards, true, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> baseTween) {
                objectMover.changeCenterCardsPosition(model.table.playCards, false, false);
                findCombinations();
            }
        });
    }

    private void removeStageListeners() {
        model.stage.getRoot().getListeners().clear();
    }

    private void findCombinations() {
        CardCombination combination = cardCombinator.calculateAndChooseCombination(getPlayCardHeap(),
                Collections.singletonList(playCard), model.table);

        if (combination.combination.isEmpty()) {
            takePlayCard();
        } else {
            combinedCards = combination.combination;
            markCombinedCards();
        }
    }

    private void takePlayCard() {
        Point destination = PositionCalculator.calcBoard(model.currentPlayer.cardPosition, model.opponents.size);
        int degree = PositionCalculator.calcRotation(model.currentPlayer.cardPosition);
        playCard.toFront();

        TweenInfo tweenInfo = playCard.tweenInfo;
        tweenInfo.x = destination.x;
        tweenInfo.y = destination.y;
        tweenInfo.angle = degree;
        tweenInfo.delay = 0.3f;
        tweenInfo.tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                turnCombinedCards.add(playCard);
                sortAndTrick();
            }
        };

        objectMover.move(playCard);
    }

    private void sortAndTrick() {
        List<Card> sortedCards = sort(turnCombinedCards);

        if (needToSort(sortedCards, turnCombinedCards) || needTakeTrick()) {
            model.fog.setVisible(true);
            model.fog.toFront();
            moveCardsToSort(sortedCards, turnCombinedCards);
        } else {
            endTurn();
        }
    }

    private void endTurn() {
        model.fog.setVisible(false);
        model.fog.toBack();
        ((User) model.currentPlayer).boardCards.addAll(turnCombinedCards);
        playCard = null;
        turnCombinedCards.clear();
        if (PlayerUtil.wasLastTurn(model)) {
            ActorHelper.takeTableCards(model, objectMover, new TweenCallback() {
                @Override
                public void onEvent(int type, BaseTween<?> source) {
                    setNextState();
                }
            });
        } else {
            setNextState();
        }
    }

    private void setNextState() {
        model.states.game = GameState.NEXT_TURN;
        model.states.cpuTurn = CpuTurn.NONE;
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
            objectMover.showOnFogLayer(card);
            initTween(card, sortedCards);
            buffer.add(card);
        } else {
            if (buffer.containsAll(initialTable) && !initialTable.isEmpty() || PlayerUtil.wasLastTurn(model)) {
                sortedCards.get(0).mark();
                model.stage.getRoot().addListener(new ClickListener() {
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
        buffer.remove(trick);
        ((User) model.currentPlayer).tricks.add(trick);
        takeTrick(trick, model.currentPlayer.cardPosition, sortedCards);
    }

    private boolean needTakeTrick() {
        return turnCombinedCards.containsAll(initialTable) && !initialTable.isEmpty() ||
                PlayerUtil.wasLastTurn(model);
    }

    private void takeTrick(Card trick, CardPosition cardPosition, final List<Card> sortedCards) {
        objectMover.showOnCardsLayer(trick);
        trick.toFront();
        Point destination = PositionCalculator.calcTrick(cardPosition, model.opponents.size);

        TweenInfo tweenInfo = trick.tweenInfo;
        tweenInfo.x = destination.x;
        tweenInfo.y = destination.y;
        tweenInfo.angle = GameConstants.ANGLE_VERTICAL;
        tweenInfo.userData = trick;
        tweenInfo.tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                ((Card) source.getUserData()).showBack();
                moveCardsToPlayer(sortedCards);
                removeStageListeners();
            }
        };

        objectMover.move(trick);
    }

    private void initTween(final Card card, final List<Card> sortedCards) {
        card.toFront();
        Point destination = new Point();
        objectMover.changeCenterCardsPosition(buffer, true, true);

        PositionCalculator.calcCenter(buffer.size(), destination, true);

        TweenInfo tweenInfo = card.tweenInfo;
        tweenInfo.x = destination.x;
        tweenInfo.y = destination.y;
        tweenInfo.angle = GameConstants.ANGLE_VERTICAL;
        tweenInfo.delay = 0;
        tweenInfo.tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                moveCardsToSort(sortedCards, turnCombinedCards);
            }
        };
        objectMover.move(card);
    }

    private void moveCardsToPlayer(List<Card> sortedCards) {
        if (!sortedCards.isEmpty()) {
            Card card = sortedCards.remove(0);
            initBackTween(card, sortedCards);
            buffer.remove(card);
            turnCombinedCards.add(card);
        } else {
            endTurn();
        }
    }

    private void initBackTween(Card card, final List<Card> sortedCards) {
        objectMover.showOnCardsLayer(card);
        card.toFront();
        Point destination = PositionCalculator.calcBoard(model.currentPlayer.cardPosition, model.opponents.size);
        int degree = PositionCalculator.calcRotation(model.currentPlayer.cardPosition);

        TweenInfo tweenInfo = card.tweenInfo;
        tweenInfo.x = destination.x;
        tweenInfo.y = destination.y;
        tweenInfo.angle = degree;
        tweenInfo.delay = GameConstants.OPPONENT_DELAY;
        tweenInfo.tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                if (!buffer.isEmpty()) {
                    objectMover.changeCenterCardsPosition(buffer, true, true);
                }

                moveCardsToPlayer(sortedCards);
            }
        };

        objectMover.move(card);
    }

    private ArrayList<Card> sort(List<Card> turnCombinedCards) {
        ArrayList<Card> cards = new ArrayList<>(turnCombinedCards);
        Collections.sort(cards, new CardsSorter());
        return cards;
    }
}

package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.domain.state.DealState;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.objectmover.ObjectMover;
import com.jjjackson.konchinka.objectmover.TweenInfo;
import com.jjjackson.konchinka.util.PlayerUtil;
import com.jjjackson.konchinka.util.PositionCalculator;

import java.util.List;
import java.util.Random;

public class PackHandler extends GameObjectHandler {

    private float jackX;
    private float jackY;

    private Pack pack;

    public PackHandler(GameModel model, ObjectMover tweenManager) {
        super(model, tweenManager);
        pack = model.pack;
    }

    @Override
    public void handle() {
        switch (model.states.deal) {
            case PACK_IN:
                movePackIn();
                break;
            case PACK_OUT:
                movePackOut();
                break;
            case DEAL:
                dealCard();
                break;
            case JACK_IN:
                moveJackToPack();
                break;
            case JACK_OUT:
                moveJackReplacementToTable();
                break;
            case WAIT:
                break;
        }
    }

    private void movePackIn() {
        pack.initPositions(model.dealerPosition);
        pack.hideOnDealerSide();
        pack.refreshTexture();
        pack.toFront();

        TweenInfo tweenInfo = pack.tweenInfo;
        tweenInfo.x = pack.getShowPosition().x;
        tweenInfo.y = pack.getShowPosition().y;
        tweenInfo.angle = GameConstants.ANGLE_VERTICAL;
        tweenInfo.speed = GameConstants.PACK_SPEED;
        tweenInfo.tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                model.states.deal = DealState.DEAL;
            }
        };

        objectMover.move(pack);

        model.states.deal = DealState.WAIT;
    }

    private void movePackOut() {
        pack.toFront();

        TweenInfo tweenInfo = pack.tweenInfo;
        tweenInfo.x = pack.getHidePosition().x;
        tweenInfo.y = pack.getHidePosition().y;
        tweenInfo.tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                model.states.deal = DealState.PACK_IN;
                model.states.game = GameState.TURN;
                model.cardHolders.removeValue(model.table, true);
            }
        };

        objectMover.move(pack);
    }

    private void dealCard() {
        model.states.deal = DealState.WAIT;
        final Card card = pack.cards.remove(0);
        pack.refreshTexture();
        initStartPosition(card, pack.getTopCardX(), pack.getTopCardY());
        Point destination = calculateDestination(model.currentPlayer);
        card.toFront();

        TweenInfo tweenInfo = card.tweenInfo;
        tweenInfo.x = destination.x;
        tweenInfo.y = destination.y;
        tweenInfo.speed = GameConstants.DEALING_SPEED;
        tweenInfo.angle = GameConstants.ANGLE_VERTICAL;
        tweenInfo.tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                CardHolder cardHolder = model.currentPlayer;
                cardHolder.playCards.add(card);
                if (cardHolder == model.player || cardHolder instanceof Table) {
                    card.showFace();
                }
                PlayerUtil.switchPlayer(model);

                if (isCardsDealt()) {
                    if (isJackOnTable() && model.turnCount == 1) {
                        model.states.deal = DealState.JACK_IN;
                        return;
                    }
                    model.states.deal = DealState.PACK_OUT;
                } else {
                    dealCard();
                }
            }
        };
        objectMover.move(card);
    }

    private void initStartPosition(Card card, int x, int y) {
        card.setX(x);
        card.setY(y);
    }

    private Point calculateDestination(CardHolder player) {
        int cardNumber = player.playCards.size();
        Point destination = new Point();
        switch (player.cardPosition) {
            case BOTTOM:
                PositionCalculator.calcBottom(cardNumber, destination);
                break;
            case LEFT:
                PositionCalculator.calcLeft(cardNumber, destination, model.opponents.size);
                break;
            case TOP:
                PositionCalculator.calcTop(cardNumber, destination);
                break;
            case RIGHT:
                PositionCalculator.calcRight(cardNumber, destination, model.opponents.size);
                break;
            case CENTER:
                objectMover.changeCenterCardsPosition(player.playCards, true);
                PositionCalculator.calcCenter(cardNumber, destination);
                break;
        }
        return destination;
    }

    private boolean isJackOnTable() {
        for (Card card : model.table.playCards) {
            if (card.value == GameConstants.JACK_VALUE) {
                return true;
            }
        }
        return false;
    }

    private boolean isCardsDealt() {
        for (CardHolder cardHolder : model.cardHolders) {
            if (cardHolder.playCards.size() != GameConstants.PLAY_CARDS_NUMBER) {
                return false;
            }
        }
        return true;
    }

    private void moveJackReplacementToTable() {
        final Card card = pack.cards.remove(0);
        initStartPosition(card, pack.getTopCardX(), pack.getTopCardY());
        card.showFace();

        TweenInfo tweenInfo = card.tweenInfo;
        tweenInfo.x = jackX;
        tweenInfo.y = jackY;
        tweenInfo.speed = GameConstants.DEALING_SPEED;
        tweenInfo.tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                model.table.playCards.add(card);

                if (isJackOnTable() && model.turnCount == 1) {
                    model.states.deal = DealState.JACK_IN;
                    return;
                }

                model.states.deal = DealState.PACK_OUT;
            }
        };

        objectMover.move(card);
        model.states.deal = DealState.WAIT;
    }

    private void moveJackToPack() {
        final Card jack = getJack(model.table.playCards);
        jackX = jack.getX();
        jackY = jack.getY();

        TweenInfo tweenInfo = jack.tweenInfo;
        tweenInfo.x = pack.getTopCardX();
        tweenInfo.y = pack.getTopCardY();
        tweenInfo.speed = GameConstants.DEALING_SPEED;
        tweenInfo.angle = GameConstants.ANGLE_VERTICAL;
        tweenInfo.tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                model.states.deal = DealState.JACK_OUT;
                pack.cards.add(getRandomPosition(10, 30), jack);
                model.table.playCards.remove(jack);
                jack.setX(-100);
                jack.setY(-100);
                jack.showBack();
            }
        };

        objectMover.move(jack);
        model.states.deal = DealState.WAIT;
    }

    private int getRandomPosition(int from, int to) {
        return to - new Random().nextInt(to - from);
    }

    private Card getJack(List<Card> cards) {
        for (Card card : cards) {
            if (card.value == GameConstants.JACK_VALUE) {
                return card;
            }
        }
        return null;
    }
}

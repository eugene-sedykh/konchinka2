package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.domain.state.DealState;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.util.PlayerUtil;
import com.jjjackson.konchinka.util.PositionCalculator;

import java.util.List;
import java.util.Random;

public class PackHandler extends GameObjectHandler {

    private float jackX;
    private float jackY;

    private Pack pack;

    public PackHandler(GameModel model, TweenManager tweenManager) {
        super(model, tweenManager);
        this.pack = model.pack;
    }

    @Override
    public void handle() {
        switch (this.model.states.deal) {
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
        this.pack.initPositions(this.model.dealerPosition);
        this.pack.hideOnDealerSide();
        this.pack.refreshTexture();

        Tween.to(this.pack, GameObject.POSITION_XY, GameConstants.PACK_SPEED).
                target(this.pack.getShowPosition().x, this.pack.getShowPosition().y).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        model.states.deal = DealState.DEAL;
                    }
                }).
                start(this.tweenManager);
        this.model.states.deal = DealState.WAIT;
    }

    private void movePackOut() {
        Tween.to(this.pack, GameObject.POSITION_XY, GameConstants.PACK_SPEED).
                target(this.pack.getHidePosition().x, this.pack.getHidePosition().y).
                start(this.tweenManager).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        model.states.deal = DealState.PACK_IN;
                        model.states.game = GameState.TURN;
                        model.cardHolders.removeValue(model.table, true);
                    }
                });
    }

    private void dealCard() {
        this.model.states.deal = DealState.WAIT;
        final Card card = this.pack.cards.remove(0);
        this.pack.refreshTexture();
        initStartPosition(card, this.pack.getTopCardX(), this.pack.getTopCardY());
        Point destination = calculateDestination(this.model.currentPlayer);
        card.toFront();

        Tween.to(card, GameObject.POSITION_XY, GameConstants.DEALING_SPEED).
                target(destination.x, destination.y).
                start(this.tweenManager).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
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
                });
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
                PositionCalculator.calcLeft(cardNumber, destination, this.model.opponents.size);
                break;
            case TOP:
                PositionCalculator.calcTop(cardNumber, destination);
                break;
            case RIGHT:
                PositionCalculator.calcRight(cardNumber, destination, this.model.opponents.size);
                break;
            case CENTER:
                this.cardMover.changeCenterCardsPosition(player.playCards, true);
                PositionCalculator.calcCenter(cardNumber, destination);
                break;
        }
        return destination;
    }

    private boolean isJackOnTable() {
        for (Card card : this.model.table.playCards) {
            if (card.value == GameConstants.JACK_VALUE) {
                return true;
            }
        }
        return false;
    }

    private boolean isCardsDealt() {
        for (CardHolder cardHolder : this.model.cardHolders) {
            if (cardHolder.playCards.size() != GameConstants.PLAY_CARDS_NUMBER) {
                return false;
            }
        }
        return true;
    }

    private void moveJackReplacementToTable() {
        final Card card = this.pack.cards.remove(0);
        initStartPosition(card, this.pack.getTopCardX(), this.pack.getTopCardY());
        card.showFace();

        Tween.to(card, GameObject.POSITION_XY, 0.2f).
                target(this.jackX, this.jackY).
                start(this.tweenManager).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        model.table.playCards.add(card);

                        if (isJackOnTable() && model.turnCount == 1) {
                            model.states.deal = DealState.JACK_IN;
                            return;
                        }

                        model.states.deal = DealState.PACK_OUT;
                    }
                });
        this.model.states.deal = DealState.WAIT;
    }

    private void moveJackToPack() {
        final Card jack = getJack(this.model.table.playCards);
        this.jackX = jack.getX();
        this.jackY = jack.getY();

        Tween.to(jack, GameObject.POSITION_XY, GameConstants.CARD_SPEED).
                target(this.pack.getTopCardX(), this.pack.getTopCardY()).
                start(this.tweenManager).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        model.states.deal = DealState.JACK_OUT;
                        pack.cards.add(getRandomPosition(10, 30), jack);
                        model.table.playCards.remove(jack);
                        jack.setX(-100);
                        jack.setY(-100);
                        jack.showBack();
                    }
                });
        this.model.states.deal = DealState.WAIT;
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

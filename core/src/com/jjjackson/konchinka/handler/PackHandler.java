package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.util.CardMover;
import com.jjjackson.konchinka.util.PositionCalculator;

import java.awt.*;

public class PackHandler extends GameObjectHandler {

    private boolean isCardMoving;
    private CardMover cardMover;

    public PackHandler(GameModel model, TweenManager tweenManager) {
        super(model, tweenManager);
        this.cardMover = new CardMover();
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
                dealCards();
                break;
            case JACK_IN:
                moveJackIn();
                break;
            case JACK_OUT:
                moveJackOut();
                break;
        }
    }

    private void movePackIn() {
        Tween.to(this.model.pack, GameObject.POSITION_XY, 0.4f).
                target(GameConstants.PACK_X, GameConstants.PACK_BOTTOM_SHOW_Y).
                start(this.tweenManager).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if (type != COMPLETE) return;

                        model.states.deal = DealState.DEAL;
                    }
                });
    }

    private void movePackOut() {

    }

    private void dealCards() {
        if (!this.isCardMoving) {
            if (isCardsDealt()) {
                if (isJackOnTable()) {
                    this.model.states.deal = DealState.JACK_IN;
                    return;
                }
                this.model.states.deal = DealState.PACK_OUT;
                return;
            }
            this.movingCard = this.model.cards.remove(0);
            initStartPosition(this.movingCard);
            initEndPosition(this.movingCard);
            this.isCardMoving = true;
        } else {
            Tween.to(this.movingCard, GameObject.POSITION_XY, 0.2f).
                    target(this.movingCard.endX, this.movingCard.endY).
                    start(this.tweenManager).
                    setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            if (type != COMPLETE) return;

                            isCardMoving = false;
                            CardHolder cardHolder = getCurrentPlayer();
                            cardHolder.playCards.add(movingCard);
                            switchPlayer();
                        }
                    });
        }
    }

    private void switchPlayer() {
        int playersNumber = this.model.cardHolders.size();
        for (int i = 0; i < playersNumber; i++) {
            CardHolder cardHolder = this.model.cardHolders.get(i);
            if (cardHolder.isCurrent) {
                cardHolder.isCurrent = false;
                int nextPlayerIndex = i == playersNumber - 1 ? 0 : ++i;
                CardHolder nextPlayer = this.model.cardHolders.get(nextPlayerIndex);
                nextPlayer.isCurrent = true;
            }
        }
    }

    private void initStartPosition(Card card) {
        card.setX(this.model.pack.getX());
        card.setY(this.model.pack.getY());
    }

    private void initEndPosition(Card card) {
        CardHolder cardHolder = getCurrentPlayer();
        Point point = calculateDestination(cardHolder);
        card.endX = point.x;
        card.endY = point.y;
    }

    private Point calculateDestination(CardHolder player) {
        int cardNumber = player.playCards.size();
        Point destination = new Point();
        switch (player.cardPosition) {
            case BOTTOM:
                PositionCalculator.calcBottom(cardNumber, destination);
                break;
            case LEFT:
                PositionCalculator.calcLeft(cardNumber, destination);
                break;
            case TOP:
                PositionCalculator.calcTop(cardNumber, destination);
                break;
            case RIGHT:
                PositionCalculator.calcRight(cardNumber, destination);
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
        return this.model.cardHolders.get(this.model.cardHolders.size() - 1).playCards.size() == 4;
    }

    private void moveJackIn() {

    }

    private void moveJackOut() {

    }
}

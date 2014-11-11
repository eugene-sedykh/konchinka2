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

    private boolean isCardMoving;
    private boolean isMovementInit;
    private float jackX;
    private float jackY;
    private Point showPackPosition = new Point();
    private Point hidePackPosition = new Point();

    public PackHandler(GameModel model, TweenManager tweenManager) {
        super(model, tweenManager);
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
        if (!isCardMoving) {
            initPackPositions(this.model.dealerPosition);
            this.isCardMoving = true;
        } else if (!this.isMovementInit) {
            this.model.pack.setX(this.hidePackPosition.x);
            this.model.pack.setY(this.hidePackPosition.y);

            Tween.to(this.model.pack, GameObject.POSITION_XY, 0.4f).
                    target(this.showPackPosition.x, showPackPosition.y).
                    start(this.tweenManager).
                    setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            if (type != COMPLETE) return;

                            model.states.deal = DealState.DEAL;
                            isCardMoving = false;
                            isMovementInit = false;
                        }
                    });
            isMovementInit = true;
        }

    }

    private void initPackPositions(CardPosition dealerPosition) {
        switch (dealerPosition) {
            case LEFT:
                this.showPackPosition.x = 150;
                this.showPackPosition.y = 350;
                this.hidePackPosition.x = -100;
                this.hidePackPosition.y = 350;
                break;
            case TOP:
                this.showPackPosition.x = 225;
                this.showPackPosition.y = 650;
                this.hidePackPosition.x = 225;
                this.hidePackPosition.y = 900;
                break;
            case RIGHT:
                this.showPackPosition.x = 330;
                this.showPackPosition.y = 350;
                this.hidePackPosition.x = 490;
                this.hidePackPosition.y = 350;
                break;
            case BOTTOM:
                this.showPackPosition.x = 225;
                this.showPackPosition.y = 225;
                this.hidePackPosition.x = 225;
                this.hidePackPosition.y = -100;
                break;
        }
    }

    private void movePackOut() {
        Tween.to(this.model.pack, GameObject.POSITION_XY, 0.4f).
                target(this.hidePackPosition.x, this.hidePackPosition.y).
                start(this.tweenManager).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if (type != COMPLETE) return;

                        model.states.deal = DealState.NONE;
                        model.states.game = GameState.TURN;
                        model.cardHolders.remove(model.table);
                    }
                });
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
            initStartPosition(this.movingCard, (int) this.model.pack.getX(), (int) this.model.pack.getY());
            initEndPosition(this.movingCard);

            CardHolder currentPlayer = getCurrentPlayer();
            if (currentPlayer == this.model.player || currentPlayer instanceof Table) {
                this.movingCard.showFace();
            }

            this.isCardMoving = true;
        } else if (!this.isMovementInit) {
            Tween.to(this.movingCard, GameObject.POSITION_XY, GameConstants.DEALING_SPEED).
                    target(this.movingCard.endX, this.movingCard.endY).
                    start(this.tweenManager).
                    setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            if (type != COMPLETE) return;

                            isCardMoving = false;
                            isMovementInit = false;
                            CardHolder cardHolder = getCurrentPlayer();
                            cardHolder.playCards.add(movingCard);
                            PlayerUtil.switchPlayer(model);
                        }
                    });
            this.isMovementInit = true;
        }
    }

    private void initStartPosition(Card card, int x, int y) {
        card.setX(x);
        card.setY(y);
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

    private void moveJackOut() {
        if (!this.isCardMoving) {
            this.movingCard = this.model.cards.remove(0);
            initStartPosition(this.movingCard, (int) this.model.pack.getX(), (int) this.model.pack.getY());
            initJackEndPosition(this.movingCard, this.jackX, this.jackY);
            this.movingCard.showFace();
            this.isCardMoving = true;
        } else if (!this.isMovementInit) {
            Tween.to(this.movingCard, GameObject.POSITION_XY, 0.2f).
                    target(this.movingCard.endX, this.movingCard.endY).
                    start(this.tweenManager).
                    setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            if (type != COMPLETE) return;

                            model.states.deal = DealState.DEAL;
                            isCardMoving = false;
                            isMovementInit = false;
                            model.table.playCards.add(movingCard);
                        }
                    });
            this.isMovementInit = true;
        }

    }

    private void moveJackIn() {
        if (!this.isCardMoving) {
            Card jack = getJack(this.model.table.playCards);
            this.jackX = jack.getX();
            this.jackY = jack.getY();
            this.movingCard = jack;
            initJackEndPosition(jack, this.model.pack.getX(), this.model.pack.getY());
            this.isCardMoving = true;
        } else if (!this.isMovementInit) {
            Tween.to(this.movingCard, GameObject.POSITION_XY, GameConstants.CARD_SPEED).
                    target(this.movingCard.endX, this.movingCard.endY).
                    start(this.tweenManager).
                    setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            if (type != COMPLETE) return;

                            model.states.deal = DealState.JACK_OUT;
                            isCardMoving = false;
                            isMovementInit = false;
                            model.cards.add(getRandomPosition(10, 30), movingCard);
                            model.table.playCards.remove(movingCard);
                            movingCard.setX(-100);
                            movingCard.setY(-100);
                            movingCard.showBack();
                        }
                    });
            this.isMovementInit = true;
        }

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

    private void initJackEndPosition(Card jack, float x, float y) {
        jack.endX = (int) x;
        jack.endY = (int) y;
    }
}

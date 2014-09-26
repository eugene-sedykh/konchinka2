package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.util.PlayerUtil;
import com.jjjackson.konchinka.util.PositionCalculator;

import java.util.ArrayList;
import java.util.List;

public class PlayerHandler extends GameObjectHandler {

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
                        initCombineListener(getCardsHeap());
                    }
                });
    }

    private boolean canCombine(Card card) {
        return cardCombinator.isCombinationPresent(getCardsHeap(), card.value) ||
                (card.value == GameConstants.JACK_VALUE && !this.model.table.playCards.isEmpty());
    }

    private void initCombineListener(List<Card> cards) {
        for (Card card : cards) {
            card.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                }
            });
        }
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
}

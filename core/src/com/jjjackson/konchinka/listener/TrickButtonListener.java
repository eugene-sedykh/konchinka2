package com.jjjackson.konchinka.listener;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.GameObject;
import com.jjjackson.konchinka.domain.User;
import com.jjjackson.konchinka.util.CardMover;

public class TrickButtonListener extends MoveCardsButtonListener {

    public TrickButtonListener(GameModel model, TweenManager tweenManager, CardMover cardMover, TextButton button) {
        super(cardMover, model, tweenManager, button);
    }

    @Override
    protected void disableOtherButtons() {
        this.model.buttons.sortButton.setTouchable(Touchable.disabled);
    }

    @Override
    protected void enableOtherButtons() {
        this.model.buttons.sortButton.setTouchable(Touchable.enabled);
    }

    @Override
    protected EventListener getCardListener(final Card card) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                card.getListeners().clear();
                removeTrickFromEverywhere();
                model.player.tricks.add(card);
                model.isTrickTaken = true;
                model.buttons.trickButton.setVisible(false);
                model.buttons.endButton.setVisible(true);
                initMovement(card);
            }

            private void removeTrickFromEverywhere() {
                sortCards.remove(card);
                for (User opponent : model.opponents) {
                    opponent.boardCards.remove(card);
                }
                model.table.playCards.remove(card);
            }
        };
    }

    private void initMovement(final Card card) {
        card.toFront();
        Tween.to(card, GameObject.ROTATION_XY, 0.2f).
                target(GameConstants.BOTTOM_TRICK_X, GameConstants.BOTTOM_TRICK_Y, 0).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        card.showBack();
                        moveSortedCards();
                    }
                }).start(this.tweenManager);
    }

}

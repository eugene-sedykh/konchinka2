package com.jjjackson.konchinka.listener;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.User;
import com.jjjackson.konchinka.objectmover.ObjectMover;
import com.jjjackson.konchinka.objectmover.TweenInfo;

public class TrickButtonListener extends MoveCardsButtonListener {

    public TrickButtonListener(GameModel model, ObjectMover objectMover, TextButton button) {
        super(objectMover, model, button);
    }

    @Override
    protected void disableOtherButtons() {
        model.buttons.sortButton.setTouchable(Touchable.disabled);
    }

    @Override
    protected void enableOtherButtons() {
        model.buttons.sortButton.setTouchable(Touchable.enabled);
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
        objectMover.showOnCardsLayer(card);
        card.toFront();

        TweenInfo tweenInfo = card.tweenInfo;
        tweenInfo.x = GameConstants.TRICK_BOTTOM_X;
        tweenInfo.y = GameConstants.TRICK_BOTTOM_Y;
        tweenInfo.angle = GameConstants.ANGLE_VERTICAL;
        tweenInfo.speed = GameConstants.CARD_SPEED;
        tweenInfo.tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                card.showBack();
                moveSortedCards();
            }
        };

        objectMover.move(card);
    }

}

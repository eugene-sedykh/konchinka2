package com.jjjackson.konchinka.listener;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.objectmover.ObjectMover;
import com.jjjackson.konchinka.objectmover.TweenInfo;

public class SortButtonListener extends MoveCardsButtonListener {

    public SortButtonListener(GameModel model, ObjectMover objectMover, TextButton button) {
        super(objectMover, model, button);
    }

    @Override
    protected void disableOtherButtons() {
        model.buttons.trickButton.setTouchable(Touchable.disabled);
        model.buttons.endButton.setTouchable(Touchable.disabled);
    }

    @Override
    protected void enableOtherButtons() {
        model.buttons.trickButton.setTouchable(Touchable.enabled);
        model.buttons.endButton.setTouchable(Touchable.enabled);
    }

    @Override
    protected ClickListener getCardListener(final Card card) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                moveCardToPlayer(card);
            }
        };
    }

    private void moveCardToPlayer(Card card) {
        card.getListeners().clear();
        initMovement(card);
        model.turnCombinedCards.add(card);
        sortCards.remove(card);
    }

    private void initMovement(Card card) {
        objectMover.showOnCardsLayer(card);
        card.toFront();

        TweenInfo tweenInfo = card.tweenInfo;
        tweenInfo.x = GameConstants.BOARD_BOTTOM_X;
        tweenInfo.y = GameConstants.BOARD_BOTTOM_Y;
        tweenInfo.angle = GameConstants.ANGLE_HORIZONTAL;
        tweenInfo.speed = GameConstants.CARD_SPEED;
        tweenInfo.tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                if (!sortCards.isEmpty()) {
                    objectMover.changeCenterCardsPosition(sortCards, false, true);
                } else {
                    endSorting();
                }
            }
        };

        objectMover.move(card);
    }

}

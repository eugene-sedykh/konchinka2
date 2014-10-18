package com.jjjackson.konchinka.listener;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.GameObject;
import com.jjjackson.konchinka.util.CardMover;

public class SortButtonListener extends MoveCardsButtonListener {

    public SortButtonListener(GameModel model, TweenManager tweenManager, CardMover cardMover, TextButton button) {
        super(cardMover, model, tweenManager, button);
    }

    @Override
    protected void disableOtherButtons() {
        this.model.buttons.trickButton.setTouchable(Touchable.disabled);
        this.model.buttons.endButton.setTouchable(Touchable.disabled);
    }

    @Override
    protected void enableOtherButtons() {
        this.model.buttons.trickButton.setTouchable(Touchable.enabled);
        this.model.buttons.endButton.setTouchable(Touchable.enabled);
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
        this.model.turnCombinedCards.add(card);
        this.sortCards.remove(card);
    }

    private void initMovement(Card card) {
        card.toFront();
        Tween.to(card, GameObject.ROTATION_XY, 0.2f).
                target(GameConstants.BOTTOM_BOARD_X, GameConstants.BOTTOM_BOARD_Y, 90).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if (!sortCards.isEmpty()) {
                            cardMover.changeCenterCardsPosition(sortCards, false, true);
                        } else {
                            endSorting();
                        }
                    }
                }).start(this.tweenManager);
    }

}

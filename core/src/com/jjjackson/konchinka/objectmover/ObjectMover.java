package com.jjjackson.konchinka.objectmover;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.GameObject;
import com.jjjackson.konchinka.util.ActorHelper;

import java.util.List;

public class ObjectMover {
    private final Group fogLayer;
    private final Group cardsLayer;
    private final TweenManager tweenManager;

    public ObjectMover(Stage stage, TweenManager tweenManager) {
        this.tweenManager = tweenManager;
        this.fogLayer = ActorHelper.getLayerByName(stage.getActors(), GameConstants.TOP_LAYER_NAME);
        this.cardsLayer = ActorHelper.getLayerByName(stage.getActors(), GameConstants.BOTTOM_LAYER_NAME);
    }

    public void changeCenterCardsPosition(List<Card> cards, boolean addPlaceholder) {
        changeCenterCardsPosition(cards, addPlaceholder, false);
    }

    public void changeCenterCardsPosition(List<Card> cards, boolean addPlaceholder, boolean shiftVertically) {
        int size = cards.size();
        int rows = (int) Math.ceil((double) size / 4d);
        int row = 0;
        for (int i = 0; i < size; i++) {
            int indexInRow = i % 4;
            if (indexInRow == 0) {
                row++;
            }
            Card card = cards.get(i);

            int x = 240 - (GameConstants.CARD_WIDTH + 4) * getRowSize(addPlaceholder, size, row, rows) / 2 +
                    indexInRow * (GameConstants.CARD_WIDTH + 4);
            card.setX(x);
            int y = 475 - ((row - 1) * (GameConstants.CARD_HEIGHT + GameConstants.TABLE_CARDS_GAP));
            card.setY(y);
            if (shiftVertically) {
                card.setY(card.getY() - (GameConstants.CARD_HEIGHT + GameConstants.TABLE_CARDS_GAP) / 2);
            }
        }
    }

    private int getRowSize(boolean addPlaceholder, int total, int row, int rows) {
        int totalInRow = total % 4;
        if (row < rows || totalInRow == 0) {
            return 4;
        }

        return totalInRow + (addPlaceholder ? 1 : 0);
    }

    public void showOnCardsLayer(List<Card> cards) {
        for (Card card : cards) {
            showOnCardsLayer(card);
        }
    }

    public void showOnCardsLayer(Card card) {
        fogLayer.removeActor(card);
        cardsLayer.addActor(card);
    }

    public void showOnFogLayer(Card card) {
        cardsLayer.removeActor(card);
        fogLayer.addActor(card);
    }

    public <T extends GameObject> void move(List<T> objects, boolean isSequenced, TweenCallback tweenCallback) {
        Timeline timeline = isSequenced ? Timeline.createSequence() : Timeline.createParallel();

        for (T object : objects) {
            timeline.push(createTween(object));
        }

        timeline.setCallbackTriggers(TweenCallback.COMPLETE)
                .setCallback(tweenCallback)
                .start(tweenManager);
    }

    public <T extends GameObject> void move(T gameObject) {
        createTween(gameObject).start(tweenManager);
    }

    private <T extends GameObject> Tween createTween(final T gameObject) {
        return Tween.to(gameObject, GameObject.ROTATION_XY, gameObject.tweenInfo.speed).
                target(gameObject.tweenInfo.x, gameObject.tweenInfo.y, gameObject.tweenInfo.angle).
                setCallbackTriggers(TweenCallback.COMPLETE | TweenCallback.BEGIN).
                setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if (type == BEGIN) {
                            gameObject.toFront();
                        } else if (gameObject.tweenInfo.tweenCallback != null) {
                            gameObject.tweenInfo.tweenCallback.onEvent(type, source);
                        }
                    }
                }).
                delay(gameObject.tweenInfo.delay).
                setUserData(gameObject.tweenInfo.userData);
    }

    public <T extends GameObject> void mark(List<T> objects, TweenCallback tweenCallback) {
        Timeline sequence = Timeline.createSequence();
        for (T object : objects) {
            sequence.push(initMarking(object));
        }
        sequence.start(tweenManager).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(tweenCallback);
    }

    private <T extends GameObject> Tween initMarking(T object) {
        return Tween.to(object, GameObject.COLOR, GameConstants.CARD_SPEED).
                target(Color.GRAY.r, Color.GRAY.g, Color.GRAY.b).
                delay(0.5f);
    }
}
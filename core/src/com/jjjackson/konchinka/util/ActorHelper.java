package com.jjjackson.konchinka.util;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.objectmover.ObjectMover;
import com.jjjackson.konchinka.objectmover.TweenInfo;

import java.util.List;

public class ActorHelper {

    public static Group getLayerByName(Array<Actor> actors, String layerName) {
        for (Actor actor : actors) {
            if (actor instanceof Group) {
                if (layerName.equals(actor.getName())) {
                    return (Group) actor;
                } else {
                    Group group = getLayerByName(((Group) actor).getChildren(), layerName);
                    if (group != null) return group;
                }
            }
        }
        return null;
    }

    public static void enable(List<? extends Actor> actors) {
        for (Actor actor : actors) {
            actor.setTouchable(Touchable.enabled);
        }
    }

    public static void disable(List<? extends Actor> actors) {
        for (Actor actor : actors) {
            actor.setTouchable(Touchable.disabled);
        }
    }

    public static void takeTableCards(final GameModel model, ObjectMover objectMover, final TweenCallback callback) {
        TweenCallback tweenCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                ((User)(model.currentPlayer)).boardCards.addAll(model.table.playCards);
                model.table.playCards.clear();
                callback.onEvent(type, source);
            }
        };
        for (Card card : model.table.playCards) {
            initCardTween(card, model.currentPlayer.cardPosition, model.opponents.size);
        }
        objectMover.move(model.table.playCards, true, tweenCallback);
    }

    private static void initCardTween(Card card, CardPosition cardPosition, int opponentsNumber) {
        card.toFront();
        Point destination = PositionCalculator.calcBoard(cardPosition, opponentsNumber);
        int rotation = PositionCalculator.calcRotation(cardPosition);

        TweenInfo tweenInfo = card.tweenInfo;
        tweenInfo.x = destination.x;
        tweenInfo.y = destination.y;
        tweenInfo.angle = rotation;
        tweenInfo.tweenCallback = null;
        tweenInfo.delay = 0.3f;
    }
}

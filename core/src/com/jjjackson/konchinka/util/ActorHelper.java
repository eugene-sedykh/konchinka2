package com.jjjackson.konchinka.util;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;

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

    public static void takeTableCards(GameModel model, TweenManager tweenManager, TweenCallback tweenCallback) {
        Timeline timeline = Timeline.createSequence();
        for (Card card : model.table.playCards) {
            timeline.push(createCardTween(card, model.currentPlayer.cardPosition, model.opponents.size));
        }
        timeline.setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(tweenCallback).
                start(tweenManager);
    }

    private static Tween createCardTween(Card card, CardPosition cardPosition, int opponentsNumber) {
        Point destination = PositionCalculator.calcBoard(cardPosition, opponentsNumber);
        int rotation = PositionCalculator.calcRotation(cardPosition);
        return Tween.to(card, GameObject.ROTATION_XY, GameConstants.CARD_SPEED).
                target(destination.x, destination.y, rotation).delay(0.3f);
    }
}

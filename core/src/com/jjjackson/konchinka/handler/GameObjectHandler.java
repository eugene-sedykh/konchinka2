package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.util.CardCombinator;
import com.jjjackson.konchinka.util.CardMover;
import com.jjjackson.konchinka.util.PositionCalculator;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObjectHandler {

    protected final GameModel model;
    protected final TweenManager tweenManager;
    protected CardCombinator cardCombinator;
    protected CardMover cardMover;

    public GameObjectHandler(GameModel model, TweenManager tweenManager) {
        this.model = model;
        this.tweenManager = tweenManager;
        this.cardCombinator = new CardCombinator();
        this.cardMover = new CardMover(this.model.stage);
    }

    public abstract void handle();

    protected void moveCardToTable(final Card card, TweenCallback tweenCallback) {
        this.cardMover.changeCenterCardsPosition(this.model.table.playCards, true);
        Point destination = new Point();
        PositionCalculator.calcCenter(this.model.table.playCards.size(), destination);
        card.toFront();
        Tween.to(card, GameObject.POSITION_XY, GameConstants.CARD_SPEED).
                target(destination.x, destination.y).
                start(this.tweenManager).
                setCallbackTriggers(TweenCallback.COMPLETE).
                setCallback(tweenCallback).delay(GameConstants.PLAY_CARD_TO_TABLE_DELAY);
    }

    protected List<UserAvatar> getAvatars() {
        List<UserAvatar> avatars = new ArrayList<>();

        avatars.add(this.model.player.avatar);
        for (User opponent : this.model.opponents) {
            avatars.add(opponent.avatar);
        }

        return avatars;
    }

    protected Tween createAvatarTween(UserAvatar avatar, Point destination) {
        return Tween.to(avatar, GameObject.POSITION_XY, GameConstants.AVATAR_SPEED).
                target(destination.x, destination.y);
    }
}

package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.TweenCallback;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.objectmover.ObjectMover;
import com.jjjackson.konchinka.objectmover.TweenInfo;
import com.jjjackson.konchinka.util.CardCombinator;
import com.jjjackson.konchinka.util.PositionCalculator;

import java.util.ArrayList;
import java.util.List;

public abstract class GameObjectHandler {

    protected final GameModel model;
    protected CardCombinator cardCombinator;
    protected ObjectMover objectMover;

    public GameObjectHandler(GameModel model, ObjectMover objectMover) {
        this.model = model;
        this.cardCombinator = new CardCombinator();
        this.objectMover = objectMover;
    }

    public abstract void handle();

    protected void moveCardToTable(final Card card, TweenCallback tweenCallback) {
        objectMover.changeCenterCardsPosition(model.table.playCards, true);
        Point destination = new Point();
        PositionCalculator.calcCenter(model.table.playCards.size(), destination);
        card.toFront();

        TweenInfo tweenInfo = card.tweenInfo;
        tweenInfo.x = destination.x;
        tweenInfo.y = destination.y;
        tweenInfo.angle = card.getRotation();
        tweenInfo.delay = GameConstants.PLAY_CARD_TO_TABLE_DELAY;
        tweenInfo.tweenCallback = tweenCallback;

        objectMover.move(card);
    }

    protected List<UserAvatar> getAvatars() {
        List<UserAvatar> avatars = new ArrayList<>();

        avatars.add(model.player.avatar);
        for (User opponent : model.opponents) {
            avatars.add(opponent.avatar);
        }

        return avatars;
    }
}

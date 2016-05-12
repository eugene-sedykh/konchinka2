package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.User;
import com.jjjackson.konchinka.domain.UserAvatar;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.domain.state.NewGameState;
import com.jjjackson.konchinka.objectmover.ObjectMover;
import com.jjjackson.konchinka.objectmover.TweenInfo;
import com.jjjackson.konchinka.util.ActorHelper;
import com.jjjackson.konchinka.util.PlayerUtil;

import java.util.Collections;
import java.util.List;

public class NewGameHandler extends GameObjectHandler {
    public NewGameHandler(GameModel model, ObjectMover objectMover) {
        super(model, objectMover);
    }

    @Override
    public void handle() {
        switch (model.states.newGame) {
            case INIT:
                prepare();
                model.states.newGame = NewGameState.WAIT;
                break;
            case WAIT:
                break;
        }
    }

    private void prepare() {
        hideResultsLayer();
        moveAvatarsBack();
        addTableToCardHolders();
        reloadTurnCount();
    }

    private void reloadTurnCount() {
        model.turnCount = 1;
    }

    private void addTableToCardHolders() {
        model.dealerPosition = model.currentPlayer.cardPosition;
        PlayerUtil.prepareCardHoldersForDealing(model, model.dealerPosition);
    }

    private void hideResultsLayer() {
        Group resultLayer = ActorHelper.getLayerByName(model.stage.getActors(), GameConstants.RESULT_LAYER_NAME);
        resultLayer.clearChildren();
    }

    private void moveAvatarsBack() {

        List<UserAvatar> avatars = getAvatars();
        for (UserAvatar avatar : avatars) {
            TweenInfo tweenInfo = avatar.tweenInfo;
            tweenInfo.x = avatar.getAvatarX();
            tweenInfo.y = avatar.getAvatarY();
        }

        objectMover.move(avatars, false, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> baseTween) {
                preparePack();
                showCardsLayer();
                model.states.game = GameState.DEAL;
                model.states.newGame = NewGameState.INIT;
            }
        });
    }

    private void preparePack() {
        gatherCardsAndShuffle();
        moveCardsToPack();
        model.pack.refreshTexture();
    }

    private void moveCardsToPack() {
        for (Card card : model.pack.cards) {
            card.setX(model.pack.getX());
            card.setY(model.pack.getY());
            card.setRotation(0);
            card.tweenInfo.delay = 0;
            card.tweenInfo.angle = 0;
            card.tweenInfo.speed = 0;
            card.tweenInfo.x = 0;
            card.tweenInfo.y = 0;
            card.tweenInfo.tweenCallback = null;
            card.showBack();
        }
    }

    private void showCardsLayer() {
        Group bottomLayer = ActorHelper.getLayerByName(model.stage.getActors(), GameConstants.BOTTOM_LAYER_NAME);
        bottomLayer.setVisible(true);
    }

    private void gatherCardsAndShuffle() {
        getFromUser(model.pack.cards, model.player);

        for (User opponent : model.opponents) {
            getFromUser(model.pack.cards, opponent);
        }

        Collections.shuffle(model.pack.cards);
    }

    private void getFromUser(List<Card> cards, User user) {
        cards.addAll(user.boardCards);
        cards.addAll(user.tricks);
        cards.addAll(user.playCards);

        user.boardCards.clear();
        user.tricks.clear();
        user.playCards.clear();
    }
}

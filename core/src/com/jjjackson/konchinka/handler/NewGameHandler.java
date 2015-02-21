package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.domain.state.NewGameState;
import com.jjjackson.konchinka.util.ActorHelper;
import com.jjjackson.konchinka.util.PlayerUtil;

import java.util.Collections;
import java.util.List;

public class NewGameHandler extends GameObjectHandler {
    public NewGameHandler(GameModel model, TweenManager tweenManager) {
        super(model, tweenManager);
    }

    @Override
    public void handle() {
        switch (this.model.states.newGame) {
            case INIT:
                prepare();
                this.model.states.newGame = NewGameState.WAIT;
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
        this.model.turnCount = 1;
    }

    private void addTableToCardHolders() {
        model.dealerPosition = model.currentPlayer.cardPosition;
        PlayerUtil.prepareCardHoldersForDealing(model, model.dealerPosition);
    }

    private void hideResultsLayer() {
        Group resultLayer = ActorHelper.getLayerByName(this.model.stage.getActors(), GameConstants.RESULT_LAYER_NAME);
        resultLayer.clearChildren();
    }

    private void moveAvatarsBack() {
        Timeline timeline = Timeline.createParallel();
        for (UserAvatar userAvatar : getAvatars()) {
            timeline.push(createAvatarTween(userAvatar, new Point(userAvatar.getAvatarX(), userAvatar.getAvatarY())));
        }
        timeline.setCallbackTriggers(TweenCallback.COMPLETE)
                .setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        preparePack();
                        showCardsLayer();
                        model.states.game = GameState.DEAL;
                        model.states.newGame = NewGameState.INIT;
                    }
                })
                .start(this.tweenManager);
    }

    private void preparePack() {
        gatherCardsAndShuffle();
        moveCardsToPack();
        this.model.pack.refreshTexture();
    }

    private void moveCardsToPack() {
        for (Card card : this.model.pack.cards) {
            card.setX(this.model.pack.getX());
            card.setY(this.model.pack.getY());
            card.setRotation(0);
            card.showBack();
        }
    }

    private void showCardsLayer() {
        Group bottomLayer = ActorHelper.getLayerByName(this.model.stage.getActors(), GameConstants.BOTTOM_LAYER_NAME);
        bottomLayer.setVisible(true);
    }

    private void gatherCardsAndShuffle() {
        getFromUser(this.model.pack.cards, model.player);

        for (User opponent : model.opponents) {
            getFromUser(this.model.pack.cards, opponent);
        }

        Collections.shuffle(this.model.pack.cards);
    }

    private void getFromUser(List<Card> cards, User user) {
        cards.addAll(user.boardCards);
        cards.addAll(user.tricks);

        user.boardCards.clear();
        user.tricks.clear();
    }
}

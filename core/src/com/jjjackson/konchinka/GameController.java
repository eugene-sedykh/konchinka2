package com.jjjackson.konchinka;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.handler.GameObjectHandlerFactory;
import com.jjjackson.konchinka.objectmover.ObjectMover;

public class GameController {
    public GameModel model;
    private TweenManager tweenManager;
    private GameObjectHandlerFactory handlerFactory;

    public GameController(GameModel gameModel) {
        this.model = gameModel;
        this.tweenManager = new TweenManager();
        Tween.setCombinedAttributesLimit(4);
        ObjectMover objectMover = new ObjectMover(model.stage, tweenManager);
        handlerFactory = new GameObjectHandlerFactory(model, objectMover);
    }

    public void update(float delta) {
        handlerFactory.get(model.states.game).handle();

        tweenManager.update(delta);
    }
}

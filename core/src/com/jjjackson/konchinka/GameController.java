package com.jjjackson.konchinka;

import aurelienribon.tweenengine.TweenManager;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.handler.GameObjectHandlerFactory;

public class GameController {
    public GameModel model;
    private TweenManager tweenManager;
    private GameObjectHandlerFactory handlerFactory;

    public GameController(GameModel gameModel) {
        this.model = gameModel;
        this.tweenManager = new TweenManager();
        this.handlerFactory = new GameObjectHandlerFactory(this.model, this.tweenManager);
    }

    public void update(float delta) {
        this.handlerFactory.get(this.model.states.game).handle();

        this.tweenManager.update(delta);
    }
}

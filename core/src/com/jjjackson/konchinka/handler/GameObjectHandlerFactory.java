package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.TweenManager;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.GameState;

public class GameObjectHandlerFactory {

    public GameObjectHandler packHandler;

    public GameObjectHandlerFactory(GameModel model, TweenManager tweenManager) {
        this.packHandler = new PackHandler(model, tweenManager);
    }

    public GameObjectHandler get(GameState gameState) {
        switch (gameState) {
            case NONE:
                return this.packHandler;
            default:
                assert false;
                return null;
        }
    }
}

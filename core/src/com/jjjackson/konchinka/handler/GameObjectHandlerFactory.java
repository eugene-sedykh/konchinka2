package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.TweenManager;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.state.GameState;

public class GameObjectHandlerFactory {

    private GameModel model;
    private final GameObjectHandler packHandler;
    private final GameObjectHandler playerHandler;
    private final GameObjectHandler nextTurnHandler;
    private final GameObjectHandler opponentHandler;

    public GameObjectHandlerFactory(GameModel model, TweenManager tweenManager) {
        this.model = model;
        this.packHandler = new PackHandler(model, tweenManager);
        this.playerHandler = new PlayerHandler(model, tweenManager);
        this.nextTurnHandler = new NextTurnHandler(model, tweenManager);
        this.opponentHandler = new OpponentHandler(model, tweenManager);
    }

    public GameObjectHandler get(GameState gameState) {
        switch (gameState) {
            case NONE:
                return this.packHandler;
            case NEXT_TURN:
                return this.nextTurnHandler;
            case TURN:
                return this.model.currentPlayer == this.model.player ? this.playerHandler : this.opponentHandler;
            default:
                assert false;
                return null;
        }
    }
}

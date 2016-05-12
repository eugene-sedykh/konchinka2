package com.jjjackson.konchinka.handler;

import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.objectmover.ObjectMover;

public class GameObjectHandlerFactory {

    private GameModel model;
    private final GameObjectHandler packHandler;
    private final GameObjectHandler playerHandler;
    private final GameObjectHandler nextTurnHandler;
    private final GameObjectHandler opponentHandler;
    private final GameObjectHandler resultHandler;
    private final GameObjectHandler newGameHandler;

    public GameObjectHandlerFactory(GameModel model, ObjectMover objectMover) {
        this.model = model;
        packHandler = new PackHandler(model, objectMover);
        playerHandler = new PlayerHandler(model, objectMover);
        nextTurnHandler = new NextTurnHandler(model, objectMover);
        opponentHandler = new OpponentHandler(model, objectMover);
        resultHandler = new ResultHandler(model, objectMover);
        newGameHandler = new NewGameHandler(model, objectMover);
    }

    public GameObjectHandler get(GameState gameState) {
        switch (gameState) {
            case DEAL:
                return packHandler;
            case NEXT_TURN:
                return nextTurnHandler;
            case TURN:
                return model.currentPlayer == model.player ? playerHandler : opponentHandler;
            case GAME_RESULT:
                return resultHandler;
            case NEW_GAME:
                return newGameHandler;
            default:
                throw new AssertionError("Cannot find handler for game state: " + gameState);
        }
    }
}

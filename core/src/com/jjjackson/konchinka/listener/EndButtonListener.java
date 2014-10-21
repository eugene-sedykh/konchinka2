package com.jjjackson.konchinka.listener;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.domain.state.TurnState;

public class EndButtonListener extends ClickListener {

    private GameModel model;
    private TweenManager tweenManager;

    public EndButtonListener(GameModel model, TweenManager tweenManager) {
        this.model = model;
        this.tweenManager = tweenManager;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {

        for (User opponent : this.model.opponents) {
            opponent.boardCards.removeAll(this.model.turnCombinedCards);
        }
        this.model.table.playCards.removeAll(this.model.turnCombinedCards);

        this.model.player.boardCards.addAll(this.model.turnCombinedCards);
        this.model.turnCombinedCards.clear();

        if (this.model.playCard != null) {
            playCardToBoard();
        }

        this.model.buttons.sortButton.setVisible(false);
        this.model.buttons.endButton.setVisible(false);

        this.model.states.game = GameState.NEXT_TURN;
        this.model.states.turn = TurnState.NONE;
    }

    private void playCardToBoard() {
        movePlayCard(this.model.playCard);
        this.model.player.boardCards.add(this.model.playCard);
        this.model.playCard = null;
    }

    private void movePlayCard(Card card) {
        card.toFront();
        Tween.to(card, GameObject.ROTATION_XY, 0.2f).
                target(GameConstants.BOTTOM_BOARD_X, GameConstants.BOTTOM_BOARD_Y, 90).
                start(this.tweenManager);
    }
}

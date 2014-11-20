package com.jjjackson.konchinka.listener;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.domain.state.TurnState;

import java.util.List;

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
        this.model.isTrickTaken = false;

        removeAllListeners();

        if (this.model.playCard != null) {
            playCardToBoard();
        }

        this.model.buttons.sortButton.setVisible(false);
        this.model.buttons.endButton.setVisible(false);

        this.model.states.game = GameState.NEXT_TURN;
        this.model.states.turn = TurnState.INIT_PLAY_CARDS;
    }

    private void removeAllListeners() {
        for (CardHolder cardHolder : this.model.cardHolders) {
            User user = (User)cardHolder;
            removeListeners(user.boardCards);
            removeListeners(user.tricks);
        }
        removeListeners(this.model.table.playCards);
        removeListeners(this.model.player.playCards);
    }

    private void removeListeners(List<Card> cards) {
        for (Card card : cards) {
            card.getListeners().clear();
        }
    }

    private void playCardToBoard() {
        movePlayCard(this.model.playCard);
        this.model.player.boardCards.add(this.model.playCard);
        this.model.playCard = null;
    }

    private void movePlayCard(Card card) {
        card.toFront();
        Tween.to(card, GameObject.ROTATION_XY, GameConstants.CARD_SPEED).
                target(GameConstants.BOTTOM_BOARD_X, GameConstants.BOTTOM_BOARD_Y, 90).
                start(this.tweenManager);
    }
}

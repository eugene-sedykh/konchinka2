package com.jjjackson.konchinka.listener;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.domain.state.TurnState;
import com.jjjackson.konchinka.util.ActorHelper;
import com.jjjackson.konchinka.util.PlayerUtil;

import java.util.List;

public class EndButtonListener extends ClickListener {

    private GameModel model;
    private TweenManager tweenManager;
    private List<Card> combinedCards;

    public EndButtonListener(GameModel model, TweenManager tweenManager, List<Card> combinedCards) {
        this.model = model;
        this.tweenManager = tweenManager;
        this.combinedCards = combinedCards;
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

        for (Card combinedCard : this.combinedCards) {
            combinedCard.unmark();
        }

        removeAllListeners();

        if (this.model.playCard != null) {
            playCardToBoard();
        }

        this.model.buttons.sortButton.setVisible(false);
        this.model.buttons.endButton.setVisible(false);

        if (PlayerUtil.wasLastTurn(this.model) && !this.model.table.playCards.isEmpty()) {
            ActorHelper.takeTableCards(this.model, this.tweenManager, new TweenCallback() {
                @Override
                public void onEvent(int type, BaseTween<?> source) {
                    ((User)(model.currentPlayer)).boardCards.addAll(model.table.playCards);
                    model.table.playCards.clear();
                    setNextState();
                }
            });
        } else {
            setNextState();
        }
    }

    private void setNextState() {
        this.model.states.turn = model.player.playCards.isEmpty() ? TurnState.INIT_PLAY_CARDS_LISTENERS :
                TurnState.ENABLE_CARDS_AND_PLAYER;
        this.model.states.game = GameState.NEXT_TURN;
    }

    private void removeAllListeners() {
        for (CardHolder cardHolder : this.model.cardHolders) {
            User user = (User)cardHolder;
            removeListeners(user.boardCards);
            removeListeners(user.tricks);
        }
        removeListeners(this.model.table.playCards);
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
                target(GameConstants.BOARD_BOTTOM_X, GameConstants.BOARD_BOTTOM_Y, 90).
                start(this.tweenManager);
    }
}

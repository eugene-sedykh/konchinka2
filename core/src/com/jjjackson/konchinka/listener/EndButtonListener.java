package com.jjjackson.konchinka.listener;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.CardHolder;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.User;
import com.jjjackson.konchinka.domain.state.GameState;
import com.jjjackson.konchinka.domain.state.TurnState;
import com.jjjackson.konchinka.objectmover.ObjectMover;
import com.jjjackson.konchinka.objectmover.TweenInfo;
import com.jjjackson.konchinka.util.ActorHelper;
import com.jjjackson.konchinka.util.PlayerUtil;

import java.util.Collections;
import java.util.List;

public class EndButtonListener extends ClickListener {

    private GameModel model;
    private ObjectMover objectMover;
    private List<Card> combinedCards;

    public EndButtonListener(GameModel model, ObjectMover objectMover, List<Card> combinedCards) {
        this.model = model;
        this.objectMover = objectMover;
        this.combinedCards = combinedCards;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {

        for (User opponent : model.opponents) {
            opponent.boardCards.removeAll(model.turnCombinedCards);
        }
        model.table.playCards.removeAll(model.turnCombinedCards);

        model.player.boardCards.addAll(model.turnCombinedCards);
        model.turnCombinedCards.clear();
        model.isTrickTaken = false;

        for (Card combinedCard : combinedCards) {
            combinedCard.unmark();
        }

        removeAllListeners();

        if (model.playCard != null) {
            removeListeners(Collections.singletonList(model.playCard));
            playCardToBoard();
        }

        model.buttons.sortButton.setVisible(false);
        model.buttons.endButton.setVisible(false);

        if (PlayerUtil.wasLastTurn(model) && !model.table.playCards.isEmpty()) {
            ActorHelper.takeTableCards(model, objectMover, new TweenCallback() {
                @Override
                public void onEvent(int type, BaseTween<?> source) {
                    setNextState();
                }
            });
        } else {
            setNextState();
        }
    }

    private void setNextState() {
        model.states.turn = model.player.playCards.isEmpty() ? TurnState.INIT_PLAY_CARDS_LISTENERS :
                TurnState.ENABLE_CARDS_AND_PLAYER;
        model.states.game = GameState.NEXT_TURN;
    }

    private void removeAllListeners() {
        for (CardHolder cardHolder : model.cardHolders) {
            User user = (User)cardHolder;
            removeListeners(user.boardCards);
            removeListeners(user.tricks);
        }
        removeListeners(model.table.playCards);
    }

    private void removeListeners(List<Card> cards) {
        for (Card card : cards) {
            card.getListeners().clear();
        }
    }

    private void playCardToBoard() {
        movePlayCard(model.playCard);
        model.player.boardCards.add(model.playCard);
        model.playCard = null;
    }

    private void movePlayCard(Card card) {
        card.toFront();
        TweenInfo tweenInfo = card.tweenInfo;
        tweenInfo.x = GameConstants.BOARD_BOTTOM_X;
        tweenInfo.y = GameConstants.BOARD_BOTTOM_Y;
        tweenInfo.speed = GameConstants.CARD_SPEED;
        tweenInfo.angle = GameConstants.ANGLE_HORIZONTAL;
        tweenInfo.tweenCallback = null;

        objectMover.move(card);
    }
}

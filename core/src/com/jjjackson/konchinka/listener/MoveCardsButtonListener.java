package com.jjjackson.konchinka.listener;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.Point;
import com.jjjackson.konchinka.domain.User;
import com.jjjackson.konchinka.objectmover.ObjectMover;
import com.jjjackson.konchinka.objectmover.TweenInfo;
import com.jjjackson.konchinka.util.PositionCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class MoveCardsButtonListener extends ClickListener {
    protected GameModel model;
    protected ObjectMover objectMover;
    protected List<Card> sortCards = new ArrayList<>();
    private TextButton button;

    public MoveCardsButtonListener(ObjectMover objectMover, GameModel model, TextButton button) {
        this.objectMover = objectMover;
        this.model = model;
        this.button = button;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (model.fog.isVisible()) {
            moveSortedCards();
        } else {
            disableCards();
            disableOtherButtons();
            model.fog.setVisible(true);
            model.fog.toFront();
            button.toFront();
            button.setTouchable(Touchable.disabled);
            moveCardsToSort();
        }
    }

    protected abstract void disableOtherButtons();

    protected abstract void enableOtherButtons();

    private void enableCards() {
        enableCards(model.table.playCards);
        for (User opponent : model.opponents) {
            enableCards(opponent.boardCards);
        }
        if (model.playCard != null) {
            enableCards(Collections.singletonList(model.playCard));
        }
    }

    private void enableCards(List<Card> cards) {
        for (Card card : cards) {
            card.setTouchable(Touchable.enabled);
        }
    }

    private void disableCards() {
        List<Card> tableCards = new ArrayList<>(model.table.playCards);
        tableCards.removeAll(model.turnCombinedCards);
        disableCards(tableCards);
        for (User opponent : model.opponents) {
            disableCards(opponent.boardCards);
        }
        if (model.playCard != null) {
            disableCards(Collections.singletonList(model.playCard));
        }
    }

    private void disableCards(List<Card> cards) {
        for (Card card : cards) {
            if (!model.turnCombinedCards.contains(card)) {
                card.setTouchable(Touchable.disabled);
            }
        }
    }

    protected void moveSortedCards() {
        button.setTouchable(Touchable.disabled);
        for (Card card : sortCards) {
            card.getListeners().clear();
            initBackToPlayerTween(card);
        }
        Collections.reverse(sortCards);

        objectMover.move(sortCards, true, new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                model.turnCombinedCards.addAll(sortCards);
                objectMover.showOnCardsLayer(sortCards);
                sortCards.clear();
                endSorting();
                button.setTouchable(Touchable.enabled);
            }
        });
    }

    private void initBackToPlayerTween(final Card card) {
        card.toFront();

        TweenInfo tweenInfo = card.tweenInfo;
        tweenInfo.x = GameConstants.BOARD_BOTTOM_X;
        tweenInfo.y = GameConstants.BOARD_BOTTOM_Y;
        tweenInfo.angle = GameConstants.ANGLE_HORIZONTAL;
        tweenInfo.speed = GameConstants.CARD_SPEED;
        tweenInfo.delay = 0;
        tweenInfo.tweenCallback = null;
    }

    private void moveCardsToSort() {
        int cardIndex = 0;
        final List<Card> turnCombinedCards = model.turnCombinedCards;
        for (Card card : turnCombinedCards) {
            initTween(card, cardIndex++, turnCombinedCards.size());
        }
        Collections.reverse(turnCombinedCards);
        objectMover.move(turnCombinedCards, true, new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                sortCards.addAll(turnCombinedCards);
                turnCombinedCards.clear();
                button.setTouchable(Touchable.enabled);
            }
        });
    }

    private void initTween(final Card card, int cardIndex, int total) {
        objectMover.showOnFogLayer(card);
        card.toFront();
        Point destination = new Point();

        PositionCalculator.calcCenter(cardIndex, total, destination);
        card.addListener(getCardListener(card));

        TweenInfo tweenInfo = card.tweenInfo;
        tweenInfo.x = destination.x;
        tweenInfo.y = destination.y;
        tweenInfo.angle = GameConstants.ANGLE_VERTICAL;
        tweenInfo.speed = GameConstants.CARD_SPEED;
        tweenInfo.delay = 0;
        tweenInfo.tweenCallback = null;
    }

    protected abstract EventListener getCardListener(Card card);

    protected void endSorting() {
        model.fog.setVisible(false);
        enableCards();
        enableOtherButtons();
    }
}

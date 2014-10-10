package com.jjjackson.konchinka.listener;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.User;

import java.util.List;

public class SortButtonListener extends ClickListener {
    private GameModel model;

    public SortButtonListener(GameModel model) {
        this.model = model;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (this.model.fog.isVisible()) {
            this.model.fog.setVisible(false);
            enableCards();
            moveSortedCards();
        } else {
            disableCards();
            this.model.fog.setVisible(true);
            this.model.fog.toFront();
            this.model.buttons.sortButton.toFront();
            moveCardsToSort();
        }
    }

    private void enableCards() {
        enableCards(this.model.table.playCards);
        for (User opponent : this.model.opponents) {
            enableCards(opponent.boardCards);
        }
    }

    private void enableCards(List<Card> cards) {
        for (Card card : cards) {
            card.setTouchable(Touchable.enabled);
        }
    }

    private void disableCards() {
        disableCards(this.model.table.playCards);
        for (User opponent : this.model.opponents) {
            disableCards(opponent.boardCards);
        }
        //todo: disable play card if not combined
    }

    private void disableCards(List<Card> cards) {
        for (Card card : cards) {
            card.setTouchable(Touchable.disabled);
        }
    }

    private void moveSortedCards() {
        for (Card card : this.model.table.playCards) {
//            card.set
        }

    }

    private void moveCardsToSort() {
        //To change body of created methods use File | Settings | File Templates.
    }
}

package com.jjjackson.konchinka.listener;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.GameModel;

public class SortButtonListener extends ClickListener {
    private GameModel model;

    public SortButtonListener(GameModel model) {
        this.model = model;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (this.model.fog.isVisible()) {
            this.model.fog.setVisible(false);
            moveSortedCards();
        } else {
            this.model.fog.setVisible(true);
            this.model.fog.toFront();
            moveCardsToSort();
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

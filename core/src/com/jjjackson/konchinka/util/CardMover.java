package com.jjjackson.konchinka.util;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.Card;

import java.util.List;

public class CardMover {
    private final Group fogLayer;
    private final Group cardsLayer;

    public CardMover(Stage stage) {
        this.fogLayer = ActorHelper.getLayerByName(stage.getActors(), GameConstants.TOP_LAYER_NAME);
        this.cardsLayer = ActorHelper.getLayerByName(stage.getActors(), GameConstants.BOTTOM_LAYER_NAME);
    }

    public void changeCenterCardsPosition(List<Card> cards, boolean addPlaceholder) {
        changeCenterCardsPosition(cards, addPlaceholder, false);
    }

    public void changeCenterCardsPosition(List<Card> cards, boolean addPlaceholder, boolean shiftVertically) {
        int size = cards.size();
        int rows = (int)Math.ceil((double) size / 4d);
        int row = 0;
        for (int i = 0; i < size; i++) {
            int indexInRow = i % 4;
            if (indexInRow == 0) {
                row++;
            }
            Card card = cards.get(i);

            int x = 240 - (GameConstants.CARD_WIDTH + 4) * getRowSize(addPlaceholder, size, row, rows) / 2 +
                    indexInRow * (GameConstants.CARD_WIDTH + 4);
            card.setX(x);
            int y = 475 - ((row - 1) * (GameConstants.CARD_HEIGHT + GameConstants.TABLE_CARDS_GAP));
            card.setY(y);
            if (shiftVertically) {
                card.setY(card.getY() - (GameConstants.CARD_HEIGHT + GameConstants.TABLE_CARDS_GAP) / 2);
            }
        }
    }

    private int getRowSize(boolean addPlaceholder, int total, int row, int rows) {
        int totalInRow = total % 4;
        if (row < rows || totalInRow == 0) {
            return 4;
        }

        return totalInRow + (addPlaceholder ? 1 : 0);
    }

    public void showOnCardsLayer(Card card) {
        this.fogLayer.removeActor(card);
        this.cardsLayer.addActor(card);
    }

    public void showOnFogLayer(Card card) {
        this.cardsLayer.removeActor(card);
        this.fogLayer.addActor(card);
    }
}
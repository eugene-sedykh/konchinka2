package com.jjjackson.konchinka.util;

import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.Card;

import java.util.List;

public class CardMover {

    public void changeCenterCardsPosition(List<Card> cards, boolean addPlaceholder) {
        changeCenterCardsPosition(cards, addPlaceholder, false);
    }

    public void changeCenterCardsPosition(List<Card> cards, boolean addPlaceholder, boolean shiftVertically) {
        int size = cards.size();
        int rows = size / 4;
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
                card.setY(card.getY() + (GameConstants.CARD_HEIGHT + GameConstants.TABLE_CARDS_GAP) / 2);
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
}
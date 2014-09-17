package com.jjjackson.konchinka.util;

import com.jjjackson.konchinka.domain.CardHolder;
import com.jjjackson.konchinka.domain.GameModel;

public class PlayerUtil {

    public static void switchPlayer(GameModel model) {
        int playersNumber = model.cardHolders.size();
        for (int i = 0; i < playersNumber; i++) {
            CardHolder cardHolder = model.cardHolders.get(i);
            if (cardHolder.isCurrent) {
                cardHolder.isCurrent = false;
                int nextPlayerIndex = i == playersNumber - 1 ? 0 : ++i;
                CardHolder nextPlayer = model.cardHolders.get(nextPlayerIndex);
                nextPlayer.isCurrent = true;
                model.currentPlayer = nextPlayer;
            }
        }
    }
}

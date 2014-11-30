package com.jjjackson.konchinka.util;

import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.CardHolder;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.User;

import java.util.List;

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

    public static void enablePlayer(CardHolder player) {
        ((User)player).activate();
    }

    public static void disablePlayer(CardHolder player) {
        ((User)player).deactivate();
    }
}

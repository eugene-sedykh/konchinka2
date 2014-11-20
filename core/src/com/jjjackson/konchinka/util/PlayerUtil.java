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
//        switchColorPlayer(player, true);
    }

    public static void disablePlayer(CardHolder player) {
//        switchColorPlayer(player, false);
    }

    private static void switchColorPlayer(CardHolder player, boolean isEnabled) {
        User user = (User) player;
        for (Card playCard : user.playCards) {
            switchColorCard(isEnabled, playCard);
        }
        switchColorCards(user.tricks, isEnabled);
        switchColorCards(user.boardCards, isEnabled);
    }

    private static void switchColorCards(List<Card> cards, boolean isEnabled) {
        if (!cards.isEmpty()) {
            switchColorCard(isEnabled, cards.get(cards.size() - 1));
        }
    }

    private static void switchColorCard(boolean isEnabled, Card playCard) {
        if (isEnabled) {
//            playCard.setActiveColor();
        } else {
//            playCard.unmark();
        }
    }
}

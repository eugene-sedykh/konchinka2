package com.jjjackson.konchinka.util;

import com.badlogic.gdx.utils.Array;
import com.jjjackson.konchinka.domain.CardHolder;
import com.jjjackson.konchinka.domain.CardPosition;
import com.jjjackson.konchinka.domain.GameModel;
import com.jjjackson.konchinka.domain.User;

public class PlayerUtil {

    public static void switchPlayer(GameModel model) {
        int playersNumber = model.cardHolders.size;
        for (int i = 0; i < playersNumber; i++) {
            CardHolder cardHolder = model.cardHolders.get(i);
            if (cardHolder == model.currentPlayer) {
                int nextPlayerIndex = i == playersNumber - 1 ? 0 : ++i;
                model.currentPlayer = model.cardHolders.get(nextPlayerIndex);
            }
        }
    }

    public static void enablePlayer(CardHolder player) {
        ((User)player).activate();
    }

    public static void disablePlayer(CardHolder player) {
        ((User)player).deactivate();
    }

    public static void prepareCardHoldersForDealing(GameModel model, CardPosition dealerPosition) {
        sort(model.cardHolders, dealerPosition);

        model.currentPlayer = model.cardHolders.get(0);

        model.cardHolders.insert(model.cardHolders.size - 1, model.table);
    }

    private static void sort(Array<CardHolder> cardHolders, CardPosition dealerPosition) {
        int dealerIndex = getDealerIndex(cardHolders, dealerPosition);
        rotate(cardHolders, cardHolders.size - dealerIndex - 1);
    }

    private static int getDealerIndex(Array<CardHolder> cardHolders, CardPosition dealerPosition) {
        for (int i = 0; i < cardHolders.size; i++) {
            if (cardHolders.get(i).cardPosition == dealerPosition) {
                return i;
            }
        }
        return 0;
    }

    private static void rotate(Array<CardHolder> cardHolders, int distance) {
        for (int i = 0; i < distance; i++) {
            cardHolders.insert(0, cardHolders.pop());
        }
    }

    public static boolean wasLastTurn(GameModel model) {
        if (!model.pack.cards.isEmpty()) return false;
        for (CardHolder cardHolder : model.cardHolders) {
            if (!cardHolder.playCards.isEmpty()) return false;
        }
        return true;
    }
}

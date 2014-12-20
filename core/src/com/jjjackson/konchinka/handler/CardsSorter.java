package com.jjjackson.konchinka.handler;

import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.CardSuit;

import java.util.Comparator;

public class CardsSorter implements Comparator<Card> {
    @Override
    public int compare(Card lhs, Card rhs) {
        int order = -1;
        if (lhs == null) return -1 * order;
        if (rhs == null) return order;

        if (GameConstants.VALUABLE_CARDS.contains(lhs.face) && GameConstants.VALUABLE_CARDS.contains(rhs.face)) {
            if (lhs.value == rhs.value) {
                return 0;
            }
            return lhs.value > rhs.value ? -1 * order : order;
        }

        if (GameConstants.VALUABLE_CARDS.contains(lhs.face) && !GameConstants.VALUABLE_CARDS.contains(rhs.face)) {
            return order;
        }
        if (!GameConstants.VALUABLE_CARDS.contains(lhs.face) && GameConstants.VALUABLE_CARDS.contains(rhs.face)) {
            return -1 * order;
        }

        if (lhs.value == GameConstants.JACK_VALUE) return -1 * order;

        if (lhs.cardSuit == CardSuit.CLUBS && rhs.cardSuit != CardSuit.CLUBS) {
            return order;
        }
        if (lhs.cardSuit != CardSuit.CLUBS && rhs.cardSuit == CardSuit.CLUBS) {
            return -1 * order;
        }
        if (lhs.cardSuit == CardSuit.CLUBS) {
            return lhs.value > rhs.value ? -1 * order : order;
        }

        return lhs.value > rhs.value ? -1 * order : order;
    }
}
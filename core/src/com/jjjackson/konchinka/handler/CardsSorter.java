package com.jjjackson.konchinka.handler;

import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.CardSuit;

import java.util.Comparator;

public class CardsSorter implements Comparator<Card> {
    @Override
    public int compare(Card lhs, Card rhs) {
        if (lhs == null) return -1;
        if (rhs == null) return 1;

        if (GameConstants.VALUABLE_CARDS.contains(lhs.face) && GameConstants.VALUABLE_CARDS.contains(rhs.face)) {
            if (lhs.value == rhs.value) {
                return 0;
            }
            return lhs.value > rhs.value ? -1 : 1;
        }

        if (GameConstants.VALUABLE_CARDS.contains(lhs.face) && !GameConstants.VALUABLE_CARDS.contains(rhs.face)) {
            return 1;
        }
        if (!GameConstants.VALUABLE_CARDS.contains(lhs.face) && GameConstants.VALUABLE_CARDS.contains(rhs.face)) {
            return -1;
        }

        if (lhs.value == GameConstants.JACK_VALUE) return -1;

        if (lhs.cardSuit == CardSuit.CLUBS && rhs.cardSuit != CardSuit.CLUBS) {
            return 1;
        }
        if (lhs.cardSuit != CardSuit.CLUBS && rhs.cardSuit == CardSuit.CLUBS) {
            return -1;
        }
        if (lhs.cardSuit == CardSuit.CLUBS) {
            return lhs.value > rhs.value ? -1 : 1;
        }

        return lhs.value > rhs.value ? -1 : 1;
    }
}
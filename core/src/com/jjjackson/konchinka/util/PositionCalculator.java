package com.jjjackson.konchinka.util;

import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.CardPosition;
import com.jjjackson.konchinka.domain.Point;

public class PositionCalculator {

    public static void calcTop(int cardNumber, Point destination) {
        destination.x = 25 + cardNumber * (GameConstants.CARD_WIDTH + 20);
        destination.y = 800 - GameConstants.CARD_HEIGHT;
    }

    public static void calcLeft(int cardNumber, Point destination) {
        destination.x = 0;
        destination.y = GameConstants.PLAY_CARD_LEFT_Y - cardNumber * 25;
    }

    public static void calcBottom(int cardNumber, Point destination) {
        destination.x = 112 + cardNumber * (GameConstants.CARD_WIDTH + 20);
        destination.y = 0;
    }

    public static void calcRight(int cardNumber, Point destination) {
        destination.x = 480 - GameConstants.CARD_WIDTH;
        destination.y = GameConstants.PLAY_CARD_RIGHT_Y - cardNumber * 25;
    }

    public static void calcCenter(int cardNumber, Point destination, boolean shiftVertically) {
        int cardsInRow = cardNumber % 4;
        destination.x = 240 - (cardsInRow + 1) * (GameConstants.CARD_WIDTH + GameConstants.TABLE_CARDS_GAP) / 2 +
                (cardsInRow) * (GameConstants.CARD_WIDTH + GameConstants.TABLE_CARDS_GAP);
        destination.y = 475 - (cardNumber / 4 * (GameConstants.CARD_HEIGHT + GameConstants.TABLE_CARDS_GAP));
        if (shiftVertically) {
            destination.y = (destination.y - (GameConstants.CARD_HEIGHT + GameConstants.TABLE_CARDS_GAP) / 2);
        }
    }

    public static void calcCenter(int cardNumber, Point destination) {
        calcCenter(cardNumber, destination, false);
    }

    public static Point calcBoard(CardPosition cardPosition) {
        Point destination = new Point();
        switch (cardPosition) {
            case LEFT:
                destination.x = GameConstants.BOARD_LEFT_X;
                destination.y = GameConstants.BOARD_LEFT_Y;
                break;
            case TOP:
                destination.x = GameConstants.BOARD_TOP_X;
                destination.y = GameConstants.BOARD_TOP_Y;
                break;
            case RIGHT:
                destination.x = GameConstants.BOARD_RIGHT_X;
                destination.y = GameConstants.BOARD_RIGHT_Y;
                break;
            case BOTTOM:
                destination.x = GameConstants.BOARD_BOTTOM_X;
                destination.y = GameConstants.BOARD_BOTTOM_Y;
                break;
        }
        return destination;
    }

    public static int calcRotation(CardPosition cardPosition) {
        return cardPosition == CardPosition.BOTTOM || cardPosition == CardPosition.TOP ? 90 : 0;
    }

    public static Point calcTrick(CardPosition cardPosition) {
        Point destination = new Point();
        switch (cardPosition) {
            case LEFT:
                destination.x = GameConstants.TRICK_LEFT_X;
                destination.y = GameConstants.TRICK_LEFT_Y;
                break;
            case TOP:
                destination.x = GameConstants.TRICK_TOP_X;
                destination.y = GameConstants.TRICK_TOP_Y;
                break;
            case RIGHT:
                destination.x = GameConstants.TRICK_RIGHT_X;
                destination.y = GameConstants.TRICK_RIGHT_Y;
                break;
            case BOTTOM:
                destination.x = GameConstants.TRICK_BOTTOM_X;
                destination.y = GameConstants.TRICK_BOTTOM_Y;
                break;
        }
        return destination;
    }
}

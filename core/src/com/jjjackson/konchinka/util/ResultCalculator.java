package com.jjjackson.konchinka.util;

import com.badlogic.gdx.utils.Array;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.CardSuit;
import com.jjjackson.konchinka.domain.GameResult;
import com.jjjackson.konchinka.domain.User;

import java.util.*;

public class ResultCalculator {

    private static final int CARD_VALUE_TWO = 2;
    private static final int CARD_VALUE_TEN = 10;

    public void calculate(Array<User> users) {
        clearResults(users);
        calculateSpecialPoints(users);
        calculateClubsAndCards(users);
        calculateTotal(users);
    }

    private void calculateTotal(Array<User> users) {
        for (User user : users) {
            GameResult gameResult = user.gameResult;
            gameResult.total += gameResult.ace + gameResult.clubsTwo + gameResult.diamondsTen + gameResult.clubs +
                    gameResult.cards + gameResult.tricks;
        }
    }

    private void clearResults(Array<User> users) {
        for (User user : users) {
            user.gameResult.clear();
        }
    }

    private void calculateSpecialPoints(Array<User> users) {
        for (User user : users) {
            for (Card card : user.boardCards) {
                calculateSpecialPoint(user.gameResult, card);
            }
            for (Card card : user.tricks) {
                calculateSpecialPoint(user.gameResult, card);
            }
            user.gameResult.tricks = user.tricks.size();
        }
    }

    private void calculateSpecialPoint(GameResult gameResult, Card card) {
        if (card.value == GameConstants.ACE_VALUE) {
            gameResult.ace++;
        } else if (isClubsTwo(card)) {
            gameResult.clubsTwo++;
        } else if (isDiamondsTen(card)) {
            gameResult.diamondsTen++;
        }
    }

    private boolean isClubsTwo(Card card) {
        return card.value == CARD_VALUE_TWO && card.cardSuit == CardSuit.CLUBS;
    }

    private boolean isDiamondsTen(Card card) {
        return card.value == CARD_VALUE_TEN && card.cardSuit == CardSuit.DIAMONDS;
    }

    private void calculateClubsAndCards(Array<User> users) {
        calculateClubs(users);
        calculateCards(users);
    }

    private void calculateClubs(Array<User> users) {
        Map<User, Integer> usersClubsNumber = calculateUsersClubs(users);
        int maxClubsNumber = max(usersClubsNumber.values());

        List<User> maxClubsUsers = filterByMaxClubs(usersClubsNumber, maxClubsNumber);
        for (User maxClubsUser : maxClubsUsers) {
            maxClubsUser.gameResult.clubs++;
        }
    }

    private List<User> filterByMaxClubs(Map<User, Integer> usersClubsNumber, int maxClubsNumber) {
        List<User> result = new ArrayList<>();

        for (Map.Entry<User, Integer> entry : usersClubsNumber.entrySet()) {
            if (entry.getValue() == maxClubsNumber) {
                result.add(entry.getKey());
            }
        }

        return result;
    }

    private int max(Collection<Integer> integers) {
        int result = 0;

        for (Integer integer : integers) {
            if (integer > result) {
                result = integer;
            }
        }

        return result;
    }

    private Map<User, Integer> calculateUsersClubs(Array<User> users) {
        Map<User, Integer> result = new HashMap<>();

        for (User user : users) {
            result.put(user, calculateClubsNumber(user));
        }

        return result;
    }

    private Integer calculateClubsNumber(User user) {
        int result = 0;

        result += calculateClubsNumber(user.boardCards);
        result += calculateClubsNumber(user.tricks);

        return result;
    }

    private int calculateClubsNumber(List<Card> cards) {
        int result = 0;

        for (Card card : cards) {
            if (card.cardSuit == CardSuit.CLUBS) {
                result++;
            }
        }

        return result;
    }

    private void calculateCards(Array<User> users) {
        int maxCardsNumber = getMaxCardsNumber(users);
        List<User> usersHavingMaxCards = getUsersHavingMaxCards(users, maxCardsNumber);

        for (User user : usersHavingMaxCards) {
            user.gameResult.cards = usersHavingMaxCards.size() > 1 ? 1 : 2;
        }
    }

    private List<User> getUsersHavingMaxCards(Array<User> users, int maxCardsNumber) {
        List<User> maxCardsUsers = new ArrayList<>();

        for (User user : users) {
            if (maxCardsNumber == (user.boardCards.size() + user.tricks.size())) {
                maxCardsUsers.add(user);
            }
        }

        return maxCardsUsers;
    }

    private int getMaxCardsNumber(Array<User> users) {
        int max = 0;

        for (User user : users) {
            int cardsNumber = user.boardCards.size() + user.tricks.size();
            if (cardsNumber > max) {
                max = cardsNumber;
            }
        }

        return max;
    }
}

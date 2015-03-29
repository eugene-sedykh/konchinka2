package com.jjjackson.konchinka.util;

import com.badlogic.gdx.Gdx;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.Card;
import com.jjjackson.konchinka.domain.CardCombination;
import com.jjjackson.konchinka.domain.Table;
import com.jjjackson.konchinka.handler.CardsSorter;

import java.util.*;

public class CardCombinator {

    public List<Card> filterCombinableCards(List<Card> cards, int target) {
        Set<Card> combinableCards = new HashSet<>();

        Collections.sort(cards, new CardComparator());
        populateCombinableCards(cards.toArray(new Card[cards.size()]), 0, new Card[cards.size()], 0, target,
                combinableCards);

        return Arrays.asList(combinableCards.toArray(new Card[combinableCards.size()]));
    }

    private void populateCombinableCards(final Card[] data, int fromIndex,
                                      final Card[] stack, final int stacklen,
                                      final int target, Set<Card> combinableCards) {
        if (target == 0) {
            // exact match of our target. Success!
            Collections.addAll(combinableCards, Arrays.copyOf(stack, stacklen));
            return;
        }

        if (fromIndex < data.length && data[fromIndex].value > target) {
            // take advantage of sorted data.
            // we can skip all values that are too large.
            return;//fromIndex++;
        }

        while (fromIndex < data.length && data[fromIndex].value <= target) {
            // stop looping when we run out of data, or when we overflow our target.
            stack[stacklen] = data[fromIndex];
            populateCombinableCards(data, fromIndex + 1, stack, stacklen + 1, target - data[fromIndex].value,
                    combinableCards);
            fromIndex++;
        }
    }

    public List<List<Card>> getCombinations(List<Card> cards, int target) {
        List<List<Card>> combinations = new ArrayList<>();

        Collections.sort(cards, new CardComparator());
        populateCombinableCards(cards.toArray(new Card[cards.size()]), 0, new Card[cards.size()], 0, target,
                combinations);

        return combinations;
    }

    private void populateCombinableCards(final Card[] data, int fromIndex,
                                         final Card[] stack, final int stacklen,
                                         final int target, List<List<Card>> combinations) {
        if (target == 0) {
            // exact match of our target. Success!
            List<Card> combination = new ArrayList<>();
            Collections.addAll(combination, Arrays.copyOf(stack, stacklen));
            combinations.add(combination);
            return;
        }

        if (fromIndex < data.length && data[fromIndex].value > target) {
            // take advantage of sorted data.
            // we can skip all values that are too large.
            return;//fromIndex++;
        }

        while (fromIndex < data.length && data[fromIndex].value <= target) {
            // stop looping when we run out of data, or when we overflow our target.
            stack[stacklen] = data[fromIndex];
            populateCombinableCards(data, fromIndex + 1, stack, stacklen + 1, target - data[fromIndex].value,
                    combinations);
            fromIndex++;
        }
    }

    public boolean isCombinationPresent(List<Card> cards, int target) {
        Collections.sort(cards, new CardComparator());
        return populateCombinableCards(cards.toArray(new Card[cards.size()]), 0, new Card[cards.size()], 0, target);
    }

    private boolean populateCombinableCards(Card[] data, int fromIndex, Card[] stack, int stacklen, int target) {
        if (target == 0) {
            // exact match of our target. Success!
            return true;
        }

        if (fromIndex < data.length && data[fromIndex].value > target) {
            // take advantage of sorted data.
            // we can skip all values that are too large.
            return false;
        }

        while (fromIndex < data.length && data[fromIndex].value <= target) {
            // stop looping when we run out of data, or when we overflow our target.
            stack[stacklen] = data[fromIndex];
            if (populateCombinableCards(data, fromIndex + 1, stack, stacklen + 1, target - data[fromIndex].value)) {
                return true;
            }
            fromIndex++;
        }
        return false;
    }

    public CardCombination calculateAndChooseCombination(List<Card> cardsHeap, List<Card> playerCards, Table table) {
        Map<Card, List<List<Card>>> cardCombinations = new HashMap<>();
        for (Card playerCard : playerCards) {
            List<List<Card>> combinations = getCombinations(cardsHeap, playerCard.value);
            if (combinations.isEmpty()) continue;
            cardCombinations.put(playerCard, combinations);
        }
        if (cardCombinations.isEmpty() || shouldPass(playerCards, cardCombinations, table.playCards)) {
            CardCombination emptyCombination = getEmptyCombination(playerCards);
            Gdx.app.log("Empty", emptyCombination.card.face);
            if (emptyCombination.card.isJack()) {
                emptyCombination.combination = table.playCards;
            }
            return emptyCombination;
        }
        return chooseCombination(getJack(playerCards), cardCombinations, table);
    }

    private boolean shouldPass(List<Card> playerCards, Map<Card, List<List<Card>>> cardCombinations, List<Card> table) {
        return playerCards.size() > 1 && areJacksOnly(cardCombinations.keySet()) &&
                !isValuableCardPresent(cardCombinations.values()) && table.isEmpty();
    }

    private boolean isValuableCardPresent(Collection<List<List<Card>>> combinations) {
        for (List<List<Card>> combination : combinations) {
            for (List<Card> cards : combination) {
                if (isValuableCardPresent(cards)) return true;
            }
        }
        return false;
    }

    private Card getJack(List<Card> playerCards) {
        for (Card card : playerCards) {
            if (card.isJack()) {
                return card;
            }
        }
        return null;
    }

    private CardCombination getEmptyCombination(List<Card> cards) {
        CardCombination emptyCombination = new CardCombination();
        emptyCombination.card = getLowestCard(cards);
        emptyCombination.combination = Collections.EMPTY_LIST;
        return emptyCombination;
    }

    private Card getLowestCard(List<Card> cards) {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Cannot get lowest card - list is empty");
        }
        if (cards.size() < 2) return cards.get(0);

        Collections.sort(cards, new CardsSorter());
        return cards.get(0);
    }

    private CardCombination chooseCombination(Card jack, Map<Card, List<List<Card>>> cardCombinations, Table table) {
        Map<Card, List<List<Card>>> combinationsWithTrick = getCombinationsWithTrick(cardCombinations, table);
        if (!combinationsWithTrick.isEmpty()) {
            Gdx.app.log("Trick", "true");
            return chooseCombinationWithTrick(combinationsWithTrick);
        }
        if (jack != null && isValuableCardPresent(table.playCards)) {
            Gdx.app.log("Jack", "true");
            CardCombination cardCombination = buildJackCombination(jack, cardCombinations);
            if (cardCombination.combination == null || cardCombination.combination.isEmpty()) {
                cardCombination.combination = table.playCards;
            }

            return cardCombination;
        }
        Map<Card, List<List<Card>>> combinationsWithValuableCards = getCombinationsWithValuableCards(cardCombinations);
        if (!combinationsWithValuableCards.isEmpty()) {
            Gdx.app.log("Valuable", "true");
            return chooseCombinationWithValuableCards(combinationsWithValuableCards);
        }
        return chooseCombinationWithMaxCards(cardCombinations);
    }

    private CardCombination chooseCombinationWithTrick(Map<Card, List<List<Card>>> combinationsWithTrick) {
        CardCombination combinationValuable = chooseCombinationWithValuableCards(combinationsWithTrick);
        if (combinationValuable.card != null) {
            return combinationValuable;
        }

        return chooseCombinationWithMaxCards(combinationsWithTrick);
    }

    private Map<Card, List<List<Card>>> getCombinationsWithTrick(Map<Card, List<List<Card>>> cardCombinations, Table table) {
        Map<Card, List<List<Card>>> combinationsWithTrick = new HashMap<>();

        for (Map.Entry<Card, List<List<Card>>> entry : cardCombinations.entrySet()) {
            if (isCombinationWithTrick(entry.getValue(), table.playCards)) {
                combinationsWithTrick.put(entry.getKey(), entry.getValue());
            }
        }

        return combinationsWithTrick;
    }

    private boolean isCombinationWithTrick(List<List<Card>> combinations, List<Card> tableCards) {
        for (List<Card> combination : combinations) {
            if (!tableCards.isEmpty() && combination.containsAll(tableCards)) {
                return true;
            }
        }

        return false;
    }

    private CardCombination buildJackCombination(Card jack, Map<Card, List<List<Card>>> cardCombinations) {
        CardCombination cardCombination = new CardCombination();
        cardCombination.card = jack;
        List<List<Card>> combinations = cardCombinations.get(jack);
        if (combinations == null) {
            return cardCombination;
        } else {
            cardCombination.combination = findMaxValuableCombination(combinations);
        }
        return cardCombination;
    }

    private List<Card> findMaxValuableCombination(List<List<Card>> combinations) {
        int maxValuableNumber = 0;
        List<Card> result = new ArrayList<>();

        for (List<Card> combination : combinations) {
            int valuableNumber = 0;
            for (Card card : combination) {
                if (GameConstants.VALUABLE_CARDS.contains(card.face)) {
                    valuableNumber++;
                }
            }
            if (valuableNumber > maxValuableNumber) {
                result = combination;
            }
        }

        return result;
    }

    private Map<Card, List<List<Card>>> getCombinationsWithValuableCards(Map<Card, List<List<Card>>> cardCombinations) {
        Map<Card, List<List<Card>>> combinations = new HashMap<>();
        for (Map.Entry<Card, List<List<Card>>> entry : cardCombinations.entrySet()) {
            List<List<Card>> combinationsWithValuableCards = new ArrayList<>();
            for (List<Card> combination : entry.getValue()) {
                for (Card card : combination) {
                    if (GameConstants.VALUABLE_CARDS.contains(card.face)) {
                        combinationsWithValuableCards.add(combination);
                        break;
                    }
                }
            }
            if (!combinationsWithValuableCards.isEmpty()) {
                combinations.put(entry.getKey(), combinationsWithValuableCards);
            }
        }
        return combinations;
    }

    private CardCombination chooseCombinationWithValuableCards(Map<Card, List<List<Card>>> cardCombinations) {
        int maxValuableNumber = 0;
        CardCombination cardCombination = new CardCombination();
        for (Map.Entry<Card, List<List<Card>>> entry : cardCombinations.entrySet()) {
            for (List<Card> combination : entry.getValue()) {
                int valuableNumber = 0;
                for (Card card : combination) {
                    if (GameConstants.VALUABLE_CARDS.contains(card.face)) {
                        valuableNumber++;
                    }
                }
                if (valuableNumber > maxValuableNumber) {
                    cardCombination.card = entry.getKey();
                    cardCombination.combination = combination;
                }
            }
        }
        return cardCombination;
    }

    private CardCombination chooseCombinationWithMaxCards(Map<Card, List<List<Card>>> cardCombinations) {
        CardCombination cardCombination = new CardCombination();
        cardCombination.combination = new ArrayList<>();
        for (Map.Entry<Card, List<List<Card>>> entry : cardCombinations.entrySet()) {
            if (entry.getKey().isJack() && !areJacksOnly(cardCombinations.keySet())) continue;
            for (List<Card> combination : entry.getValue()) {
                if (combination.size() > cardCombination.combination.size()) {
                    cardCombination.combination = combination;
                    cardCombination.card = entry.getKey();
                }
            }
        }
        Gdx.app.log("Max", cardCombination.card != null ? cardCombination.card.face : "null");
        return cardCombination;
    }

    private boolean areJacksOnly(Set<Card> cards) {
        for (Card card : cards) {
            if (!card.isJack()) return false;
        }
        return true;
    }

    private boolean isValuableCardPresent(List<Card> cards) {
        for (Card card : cards) {
            if (GameConstants.VALUABLE_CARDS.contains(card.face)) {
                return true;
            }
        }
        return false;
    }


    private class CardComparator implements Comparator<Card> {

        @Override
        public int compare(Card lhs, Card rhs) {
            if (lhs.value == rhs.value) {
                return 0;
            }

            return lhs.value > rhs.value ? 1 : -1;
        }
    }
}

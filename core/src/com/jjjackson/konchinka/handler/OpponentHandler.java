package com.jjjackson.konchinka.handler;

import aurelienribon.tweenengine.TweenManager;
import com.jjjackson.konchinka.GameConstants;
import com.jjjackson.konchinka.domain.*;
import com.jjjackson.konchinka.domain.state.CpuTurn;

import java.util.*;

public class OpponentHandler extends GameObjectHandler {

    private final static List<String> VALUABLE_CARDS = Arrays.asList("c1", "d1", "h1", "s1", "d10", "s2");

    private Card playCard;
    private List<Card> combinableCards;

    public OpponentHandler(GameModel model, TweenManager tweenManager) {
        super(model, tweenManager);
    }

    @Override
    public void handle() {
        switch (this.model.states.cpuTurn) {
            case NONE:
                this.model.states.cpuTurn = CpuTurn.CHOOSE_PLAY_CARD;
                break;
            case CHOOSE_PLAY_CARD:
                choosePlayCard();
        }
    }

    private void choosePlayCard() {
        List<Card> playCardHeap = getPlayCardHeap();
        CardCombination bestCombination = calculateAndChooseCombination(playCardHeap, this.model.currentPlayer.playCards,
                this.model.table);
        this.playCard = bestCombination.card;
        this.combinableCards = bestCombination.combination;
    }

    private List<Card> getPlayCardHeap() {
        List<Card> cards = new ArrayList<>();

        for (CardHolder cardHolder : this.model.cardHolders) {
            if (cardHolder.isCurrent) continue;

            User user = (User) cardHolder;
            if (user.boardCards.isEmpty()) continue;
            cards.add(user.boardCards.get(user.boardCards.size() - 1));
        }
        cards.addAll(this.model.table.playCards);

        return cards;
    }

    private CardCombination calculateAndChooseCombination(List<Card> cardsHeap, List<Card> playerCards, Table table) {
        Map<Card, List<List<Card>>> cardCombinations = new HashMap<>();
        for (Card playerCard : playerCards) {
            List<List<Card>> combinations = this.cardCombinator.getCombinations(cardsHeap, playerCard.value);
            cardCombinations.put(playerCard, combinations);
        }
        if (cardCombinations.isEmpty()) {
            return getEmptyCombination(playerCards);
        }
        return chooseCombination(getJack(playerCards), cardCombinations, table);
    }

    private Card getJack(List<Card> playerCards) {
        for (Card card : playerCards) {
            if (card.value == GameConstants.JACK_VALUE) {
                return card;
            }
        }
        return null;
    }

    private CardCombination getEmptyCombination(List<Card> playerCards) {
        CardCombination emptyCombination = new CardCombination();
        emptyCombination.card = getLowestCard(playerCards);
        emptyCombination.combination = Collections.EMPTY_LIST;
        return emptyCombination;
    }
    private Card getLowestCard(List<Card> playerCards) {
        Collections.sort(playerCards, new Comparator<Card>() {
            @Override
            public int compare(Card lhs, Card rhs) {
                if (VALUABLE_CARDS.contains(lhs.face)) {
                    return (rhs.value != GameConstants.JACK_VALUE) ? 1 : -1;
                }
                if (VALUABLE_CARDS.contains(rhs.face)) {
                    return (lhs.value != GameConstants.JACK_VALUE) ? -1 : 1;
                }
                if (lhs.value == rhs.value) {
                    return 0;
                }
                return lhs.value > rhs.value ? 1 : -1;
            }
        });
        return playerCards.get(0);
    }
    private CardCombination chooseCombination(Card jack, Map<Card, List<List<Card>>> cardCombinations, Table table) {
        Map<Card, List<List<Card>>> combinationsWithTrick = getCombinationsWithTrick(cardCombinations, table);
        if (!combinationsWithTrick.isEmpty()) {
            return chooseCombinationWithTrick(combinationsWithTrick);
        }
        if (jack != null && isValuableCardPresent(table.playCards)) {
            return buildJackCombination(jack, cardCombinations, table);
        }
        Map<Card, List<List<Card>>> combinationsWithValuableCards = getCombinationsWithValuableCards(cardCombinations);
        if (!combinationsWithValuableCards.isEmpty()) {
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
            if (combination.containsAll(tableCards)) {
                return true;
            }
        }

        return false;
    }


    private CardCombination buildJackCombination(Card jack, Map<Card, List<List<Card>>> cardCombinations, Table table) {
        CardCombination cardCombination = new CardCombination();
        cardCombination.card = jack;
        List<List<Card>> combinations = cardCombinations.get(jack);
        if (combinations == null) {
            cardCombination.combination = table.playCards;
            return cardCombination;
        }
        return null;
    }

    private Map<Card, List<List<Card>>> getCombinationsWithValuableCards(Map<Card, List<List<Card>>> cardCombinations) {
        Map<Card, List<List<Card>>> combinations = new HashMap<>();
        for (Map.Entry<Card, List<List<Card>>> entry : cardCombinations.entrySet()) {
            List<List<Card>> combinationsWithValuableCards = new ArrayList<>();
            for (List<Card> combination : entry.getValue()) {
                for (Card card : combination) {
                    if (VALUABLE_CARDS.contains(card.face)) {
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
                    if (VALUABLE_CARDS.contains(card.face)) {
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
            for (List<Card> combination : entry.getValue()) {
                if (combination.size() > cardCombination.combination.size()) {
                    cardCombination.combination = combination;
                    cardCombination.card = entry.getKey();
                }
            }
        }
        return cardCombination;
    }

    private boolean isValuableCardPresent(List<Card> cards) {
        for (Card card : cards) {
            if (VALUABLE_CARDS.contains(card.face)) {
                return true;
            }
        }
        return false;
    }

}

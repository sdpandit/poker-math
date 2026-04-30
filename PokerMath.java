import java.util.*;

public class PokerMath {
    public static final int SPADES = 3;
    public static final int HEARTS = 2;
    public static final int DIAMONDS = 1;
    public static final int CLUBS = 0;

    public static final int T = 10;
    public static final int J = 11;
    public static final int Q = 12;
    public static final int K = 13;
    public static final int A = 14;

    public static void main(String[] args) {
        Card[] hole1 = new Card[2];
        hole1[0] = new Card(A, HEARTS);
        hole1[1] = new Card(K, CLUBS);
        Card[] hole2 = new Card[2];
        hole2[0] = new Card(Q, HEARTS);
        hole2[1] = new Card(Q, DIAMONDS);
        List<Card[]> hands = new ArrayList<>();
        hands.add(hole1);
        hands.add(hole2);
        System.out.println(playAllHands(hands));
    }

    // Equity
    public static double equity(Card[] holeCards, int numPlayers, int trials) {
        if (numPlayers < 2 || holeCards.length != 2) {
            throw new IllegalArgumentException();
        }
        double eq = 0;
        Deck deck = new Deck();
        for (int i=0; i<trials; i++) {
            deck.reset();
            for (Card c : holeCards) {
                deck.removeCard(c);
            }
            List<Card[]> holeList = new ArrayList<>();
            holeList.add(holeCards);

            for (int j = 1; j<numPlayers; j++) {
                Card[] oppHole = new Card[2];
                oppHole[0] = deck.dealRandomCard();
                oppHole[1] = deck.dealRandomCard();
                holeList.add(oppHole);
            }
            List<Integer> winners = randomHoldEm(holeList, false);
            if (winners.contains(0)) {
                eq+=1.0/winners.size();
            }
        }
        return eq/trials;
    }

    // Returns the best five card poker hand among the player
    // hole cards and community cards
    public static PokerHand holdEmBest(Card[] holeCards, Card[] board) {
        if (holeCards.length != 2 || board.length != 5) {
            throw new IllegalArgumentException();
        }
        Card[] pool = new Card[7];
        for (int i=0; i<2; i++) {
            pool[i] = holeCards[i];
        }
        for (int i=2; i<7; i++) {
            pool[i] = board[i-2];
        }

        PokerHand best = null;
        for (int a=0; a<7; a++) {
            for (int b=a+1; b<7; b++) {
                Set<Card> hand = new TreeSet<>();
                int i=0;
                for (Card card : pool) {
                    if (i != a && i != b) {
                        hand.add(card);
                    }
                    i++;
                }
                PokerHand candidate = new PokerHand(hand);
                if (best == null || candidate.compareTo(best) > 0) {
                    best = candidate;
                }
            }
        }
        return best;
    }

    // Returns a list of all pot winners given the players'
    // hole cards and the board run-out
    public static List<Integer> holdEmWinners
        (List<Card[]> holeList, Card[] board, boolean printHands) {
        if (holeList.isEmpty()) {
            throw new IllegalArgumentException("You must have some players!");
        }
        List<Integer> potWinners = new ArrayList<>();
        PokerHand best = null;
        for (int i=0; i<holeList.size(); i++) {
            PokerHand candidate = holdEmBest(holeList.get(i), board);
            int num  = candidate.compareTo(best);
            if (num > 0) {
                potWinners.clear();
                potWinners.add(i);
                best = candidate;
            }
            else if (num == 0) {
                potWinners.add(i);
            }
        }
        if (printHands) {System.out.println(board + " " + potWinners);}
        return potWinners;
    }

    // Generates a random board run-out and returns the winners
    public static List<Integer> randomHoldEm(List<Card[]> holeList, boolean printHands) {
        if (holeList.isEmpty()) {
            throw new IllegalArgumentException("You must have some players!");
        }
        Deck deck = new Deck();
        for (Card[] s : holeList) {
            if (s.length != 2) {
                throw new IllegalArgumentException("All players must have two hole cards");
            }
            for (Card c : s) {
                if (deck.removeCard(c) == null) {
                    throw new IllegalArgumentException("All players must have different hole cards");
                }
            }
        }

        Card[] board = new Card[5];

        for (int i=0; i<5; i++) {
            board[i] = deck.dealRandomCard();
        }
        return holdEmWinners(holeList, board, printHands);
    }

    // Simulates multiple random hands
    public static Map<Integer, Double> simHoldEm(List<Card[]> holeList, int trials,
        boolean separateSplits, boolean printHands) {
        if (holeList.isEmpty()) {
            throw new IllegalArgumentException("You must have some players!");
        }
        Map<Integer, Double> results = new TreeMap<>();
        for (int i=0; i<holeList.size(); i++) {
            results.put(i,0.0);
        }
        if (separateSplits) {results.put(-1, 0.0);}
        for (int i=0; i<trials; i++) {
            List<Integer> winners = randomHoldEm(holeList, printHands);
            if (separateSplits && winners.size() > 1) {
                results.put(-1, results.get(-1) + 1);
            }
            else {
                for (int player : winners) {
                    results.put(player, results.get(player) + 1.0/winners.size());   
                }
            }    
        }
        return results;
    }

    // Simulates every possible board run-out for the given
    // set of hole cards
    public static Map<Integer, Integer> playAllHands(List<Card[]> holeList) {
        if (holeList.isEmpty()) {
            throw new IllegalArgumentException("You must have some players!");
        }
        Map<Integer, Integer> results = new TreeMap<>();
        Deck deck = new Deck();
        for (Card[] s : holeList) {
            if (s.length != 2) {
                throw new IllegalArgumentException("All players must have two hole cards");
            }
            for (Card card : s) {
                if (deck.removeCard(card) == null) {
                    throw new IllegalArgumentException("All players must have different hole cards");
                }
            }
            results.put(results.size(), 0);
        }
        results.put(-1, 0);
        int size = deck.deckSize();
        int[] arr = {0,1,2,3,4};
        int i=0;
        do {
            if (i%100000 == 0) {System.out.println(i);}
            Card[] board = new Card[5];
            for (int j=0; j<arr.length; j++) {
                board[j] = deck.getCardAtPosition(arr[j]);
            }
            List<Integer> winners = holdEmWinners(holeList, board, false);
            if (winners.size() == 1) {
                int winner = winners.get(0);
                results.put(winner, results.get(winner) + 1);
            }
            else {
                results.put(-1, results.get(-1) + 1);
            }
            i++;
            incrementArray(arr, size);
        } while (arr[4] != 4);
        return results;
    }

    // Helper method for playAllHands
    private static void incrementArray(int[] arr, int max) {
        int i = arr.length - 1;
        while (i >= 0 && arr[i] == max + i - arr.length) {
            i--;
        }
        if (i == -1) {
            for (int j=0; j<arr.length; j++) {
                arr[j] = j;
            }
            return;
        }
        arr[i]++;
        for (int j=i+1; j<arr.length; j++) {
            arr[j] = arr[i] + (j-i);
        }
    }
}

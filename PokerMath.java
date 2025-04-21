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
        Set<Card> hole1 = new TreeSet<>();
        Set<Card> hole2 = new TreeSet<>();
        hole1.add(new Card(J, SPADES));
        hole1.add(new Card(T, SPADES));
        hole2.add(new Card(2, HEARTS));
        hole2.add(new Card(2, CLUBS));
        System.out.println(playAllHands(hole1, hole2));
    }

    public static double handEquity(Set<Card> hand, int numPlayers, int trials) {
        if (numPlayers < 2 || hand.size() != 2) {
            throw new IllegalArgumentException();
        }
        double equity = 0;
        Deck deck = new Deck();
        for (int i=0; i<trials; i++) {
            deck.reset();
            for (Card c : hand) {
                deck.removeCard(c);
            }
            List<Set<Card>> playerHands = new ArrayList<>();
            playerHands.add(hand);

            for (int j=0; j<numPlayers-1; j++) {
                Set<Card> oppHand = new TreeSet<>();
                oppHand.add(deck.dealRandomCard());
                oppHand.add(deck.dealRandomCard());
                playerHands.add(oppHand);
            }
            List<Integer> winners = randomHoldEm(playerHands, false);
            if (winners.contains(0)) {
                equity+=1.0/winners.size();
            }
        }
        return equity;
    }

    // Returns a PokerHand containing the best five card hand out of seven cards
    public static PokerHand holdEmBest(Set<Card> hand) {
        if (hand.size() != 7) {
            throw new IllegalArgumentException();
        }
        PokerHand best = null;
        for (int a=0; a<7; a++) {
            for (int b=a+1; b<7; b++) {
                Set<Card> s = new TreeSet<>();
                int i=0;
                for (Card card : hand) {
                    if (i != a && i != b) {
                        s.add(card);
                    }
                    i++;
                }
                PokerHand candidate = new PokerHand(s);
                if (best == null || candidate.compareTo(best) > 0) {
                    best = candidate;
                }
            }
        }
        return best;
    }

    public static List<Integer> holdEmWinners(List<Set<Card>> playerHands, Set<Card> community) {
        if (playerHands.isEmpty()) {
            throw new IllegalArgumentException("You must have some players!");
        }
        List<Integer> potWinners = new ArrayList<>();
        PokerHand best = null;
        for (int i=0; i<playerHands.size(); i++) {
            Set<Card> s = new TreeSet<>(playerHands.get(i));
            for (Card c : community) {
                s.add(c);
            }
            PokerHand candidate = holdEmBest(s);
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
        return potWinners;
    }

    public static List<Integer> randomHoldEm(List<Set<Card>> playerHands, boolean printHands) {
        if (playerHands.isEmpty()) {
            throw new IllegalArgumentException("You must have some players!");
        }
        Deck deck = new Deck();
        for (Set<Card> s : playerHands) {
            if (s.size() != 2) {
                throw new IllegalArgumentException("All players must have two hole cards");
            }
            for (Card c : s) {
                if (deck.removeCard(c) == null) {
                    throw new IllegalArgumentException("All players must have different hole cards");
                }
            }
        }

        Set<Card> community = new TreeSet<>();

        for (int i=0; i<5; i++) {community.add(deck.dealRandomCard());}
        
        return holdEmWinners(playerHands, community);
    }

    public static Map<Integer, Double> simHoldEm(List<Set<Card>> playerHands, int trials, boolean printHands) {
        if (playerHands.isEmpty()) {
            throw new IllegalArgumentException("You must have some players!");
        }
        Map<Integer, Double> results = new TreeMap<>();
        for (int i=0; i<playerHands.size(); i++) {
            results.put(i,0.0);
        }
        for (int i=0; i<trials; i++) {
            List<Integer> winners = randomHoldEm(playerHands, printHands);
            if (printHands) {
                System.out.print(winners + "\n");
            }
            for (int player : winners) {
                results.put(player, results.get(player) + 1.0/winners.size());
            }
        }
        return results;
    }

    public static Map<String,Integer> playAllHands(Set<Card> hole1, Set<Card> hole2) {
        Map<String, Integer> results = new TreeMap<>();
        results.put("P1", 0);
        results.put("P2", 0);
        results.put("SP", 0);
        Deck deck = new Deck();
        for (Card card : hole1) {deck.removeCard(card);}
        for (Card card : hole2) {deck.removeCard(card);}
        int[] arr = {0,1,2,3,4};
        int i=0;
        do {
            if (i%100000 == 0) {System.out.println(i);}
            Set<Card> player1Cards = new TreeSet<>(hole1);
            Set<Card> player2Cards = new TreeSet<>(hole2);
            for (int x : arr) {
                Card c = deck.getCardAtPosition(x);
                player1Cards.add(c);
                player2Cards.add(c);
            }
            int num = holdEmBest(player1Cards).compareTo(holdEmBest(player2Cards));
            if (num > 0) {
                results.put("P1",results.get("P1")+1);
            }
            else if (num < 0) {
                results.put("P2",results.get("P2")+1);
            }
            else {
                results.put("SP",results.get("SP")+1);
            }
            i++;
            increment(arr, 48);
        } while (arr[4] != 4);
        return results;
    }

    private static void increment(int[] arr, int max) {
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

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
        for (int i=2; i<=14; i++) {
            for (int j=2; j<=14; j++) {
                Set<Card> h = new TreeSet<>();
                h.add(new Card(i, HEARTS));
                if (i <= j) {h.add(new Card(j, SPADES));}
                else {h.add(new Card(j, HEARTS));}
                System.out.println(h + " " + handEquity(h, 6, 1000)/1000.0);
            }
        }
    }

    public static double handEquity(Set<Card> hand, int numPlayers, int trials) {
        if (numPlayers < 2 || hand.size() != 2) {
            throw new IllegalArgumentException();
        }
        double equity = 0;
        for (int i=0; i<trials; i++) {
            Deck deck = new Deck();
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
        Deck deck = new Deck();
        for (Set<Card> s : playerHands) {
            if (s.size() != 2) {
                throw new IllegalArgumentException("All players must have two hole cards");
            }
            for (Card c : s) {
                if (deck.removeCard(c) == false) {
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
        if (hole1.size() != 2 || hole2.size() != 2) {
            throw new IllegalArgumentException("Both players must have two hole cards");
        }
        Map<String, Integer> results = new TreeMap<>();
        results.put("Player 1 Wins", 0);
        results.put("Player 2 Wins", 0);
        results.put("Splits", 0);
        List<Card> cardList = new ArrayList<>();
        for (int i=8; i<60; i++) {
            cardList.add(new Card(i/4, i%4));
        }
        for (Card card : hole1) {
            cardList.remove(card);
        }
        for (Card card : hole2) {
            cardList.remove(card);
        }
        int i=0;
        for (int a=0; a<cardList.size(); a++) {
            for (int b=a+1; b<cardList.size(); b++) {
                for (int c=b+1; c<cardList.size(); c++) {
                    for (int d=c+1; d<cardList.size(); d++) {
                        for (int e=d+1; e<cardList.size(); e++) {
                            if (i%100000==0) {
                                System.out.println(i);
                            }
                            Set<Card> player1Cards = new TreeSet<>(hole1);
                            Set<Card> player2Cards = new TreeSet<>(hole2);
                            int[] arr = {a,b,c,d,e};
                            for (int x : arr) {
                                player1Cards.add(cardList.get(x));
                                player2Cards.add(cardList.get(x));
                            }
                            int num = holdEmBest(player1Cards).compareTo(holdEmBest(player2Cards));
                            if (num > 0) {
                                results.put("Player 1 Wins",results.get("Player 1 Wins")+1);
                            }
                            else if (num < 0) {
                                results.put("Player 2 Wins",results.get("Player 2 Wins")+1);
                            }
                            else {
                                results.put("Splits",results.get("Splits")+1);
                            }
                            i++;
                        }
                    }
                }
            }
        }
        return results;
    }
}

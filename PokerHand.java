import java.util.*;

public class PokerHand implements Comparable<PokerHand>{
    public static final int SPADES = 3;
    public static final int HEARTS = 2;
    public static final int DIAMONDS = 1;
    public static final int CLUBS = 0;

    public static final int T = 10;
    public static final int J = 11;
    public static final int Q = 12;
    public static final int K = 13;
    public static final int A = 14;

    private Set<Card> hand;
    private Map<Integer,Integer> ranks;
    private Map<Integer,Integer> suits;

    public PokerHand(Set<Card> hand) {
        if (hand.size() != 5) {
            throw new IllegalArgumentException();
        }
        this.hand = hand;
        this.ranks = new TreeMap<>();
        this.suits = new TreeMap<>();
        for (Card c : hand) {
            if (!ranks.containsKey(c.rank)) {
                ranks.put(c.rank,0);
            }
            if (!suits.containsKey(c.suit)) {
                suits.put(c.suit,0);
            }
            ranks.put(c.rank,ranks.get(c.rank)+1);
            suits.put(c.suit,suits.get(c.suit)+1);
        }
    }

    public static void main(String[] args) {
        Set<Card> hole1 = new TreeSet<>();
        Set<Card> hole2 = new TreeSet<>();

        hole1.add(new Card(8, HEARTS));
        hole1.add(new Card(8, SPADES));

        hole2.add(new Card(J, CLUBS));
        hole2.add(new Card(T, CLUBS));

        List<Set<Card>> holes = new ArrayList<>();
        holes.add(hole1);
        holes.add(hole2);

        System.out.println(playAllHands(hole1, hole2));
    }

    private boolean hasFlush() {
        return suits.size() == 1;
    }

    private boolean hasStraight() {
        if (ranks.size() == 5 && setMax(ranks.keySet()) - setMin(ranks.keySet()) == 4) {
            return true;
        }
        // check for wheel
        if (ranks.containsKey(A) && ranks.containsKey(2) && ranks.containsKey(3)
        && ranks.containsKey(4) && ranks.containsKey(5)) {
            return true;
        }
        return false;
    }

    // returns the rank of a hand
    private int handRank() {
        if (hasStraight() && hasFlush() && setMin(ranks.keySet()) == 10) {
            // royal flush
            return 9;
        }
        if (hasStraight() && hasFlush()) {
            // straight flush
            return 8;
        }
        if (setMax(ranks.values()) == 4) {
            // quads
            return 7;
        }
        if (setMax(ranks.values()) == 3 && setMin(ranks.values()) == 2) {
            // full house
            return 6;
        }
        if (hasFlush()) {
            // flush
            return 5;
        }
        if (hasStraight()) {
            // straight
            return 4;
        }
        if (setMax(ranks.values()) == 3) {
            // three of a kind
            return 3;
        }
        if (setMax(ranks.values()) == 2 && ranks.size() == 3) {
            // two pair
            return 2;
        }
        if (setMax(ranks.values()) == 2) {
            // pair
            return 1;
        }
        else {
            // nothing
            return 0;
        }
    }
    
    private int setMin(Collection<Integer> s) {
        int min = Integer.MAX_VALUE;
        for (int x : s) {
            if (x < min) {
                min = x;
            }
        }
        return min;
    }

    private int setMax(Collection<Integer> s) {
        int max = Integer.MIN_VALUE;
        for (int x : s) {
            if (x > max) {
                max = x;
            }
        }
        return max;
    }

    @Override
    public int compareTo(PokerHand other) {
        int r1 = this.handRank();
        int r2 = other.handRank();
        if (r1 != r2) {
            return r1 - r2;
        }
        PriorityQueue<Pair> thisQueue = new PriorityQueue<>();
        PriorityQueue<Pair> otherQueue = new PriorityQueue<>();
        for (int rank : this.ranks.keySet()) {
            // handle the wheel case
            if (rank == 14 && (r1 == 4 || r1 == 8) && this.ranks.containsKey(5)) {
                thisQueue.add(new Pair(1,1));
            }
            else {
                thisQueue.add(new Pair(rank, this.ranks.get(rank)));
            }
        }
        for (int rank : other.ranks.keySet()) {
            // handle the wheel case
            if (rank == 14 && (r2 == 4 || r2 == 8) && other.ranks.containsKey(5)) {
                otherQueue.add(new Pair(1,1));
            }
            else {
                otherQueue.add(new Pair(rank, other.ranks.get(rank)));
            }
        }
        while (!thisQueue.isEmpty() && !otherQueue.isEmpty()) {
            Pair a = thisQueue.remove();
            Pair b = otherQueue.remove();
            if (a.rank != b.rank) {
                return a.rank - b.rank;
            }
        }
        return 0;
    }

    public String toString() {
        String output = "";
        for (Card card : hand) {
            output = output + card.toString() + " ";
        }
        return output;
    }

    private class Pair implements Comparable<Pair> {
        int rank, quant;
    
        private Pair(int rank, int quant) {
            this.rank = rank;
            this.quant = quant;
        }
    
        public int compareTo(Pair other) {
            if (this.quant != other.quant) {
                return -(this.quant - other.quant);
            }
            return -(this.rank - other.rank);
        }
    
        public String toString() {
            return "Rank: " + rank + " Quantity: " + quant;
        }
    }

    public static double handEquity(Set<Card> hole, int numPlayers, int trials) {
        if (numPlayers < 2 || hole.size() != 2) {
            throw new IllegalArgumentException();
        }
        double equity = 0;
        for (int i=0; i<trials; i++) {
            List<Set<Card>> holeCards = new ArrayList<>();
            holeCards.add(hole);
            Set<Card> allCards = new TreeSet<>();
            for (Card card : hole) {
                allCards.add(card);
            }

            for (int j=0; j<numPlayers-1; j++) {
                Set<Card> oppHole = new TreeSet<>();
                while (oppHole.size() < 2) {
                    Card card = randomCard();
                    if (!allCards.contains(card)) {
                        oppHole.add(card);
                        allCards.add(card);
                    }
                }
                holeCards.add(oppHole);
            }
            List<Integer> winners = randomHoldEm(holeCards, false);
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
                /*if (s.contains(null)) {
                    continue;
                }*/
                PokerHand candidate = new PokerHand(s);
                if (best == null || candidate.compareTo(best) > 0) {
                    best = candidate;
                }
            }
        }
        return best;
    }

    // Returns a PokerHand containing the best five card hand out of eight cards
    public static PokerHand eightCardBest(Set<Card> hand) {
        if (hand.size() != 8) {
            throw new IllegalArgumentException();
        }
        // returns a set consisting of the best five hand card
        PokerHand best = null;
        for (int a=0; a<8; a++) {
            for (int b=a+1; b<8; b++) {
                for (int c=b+1; c<8; c++) {
                    Set<Card> s = new TreeSet<>();
                    int i=0;
                    for (Card card : hand) {
                        if (i != a && i != b && i != c) {
                            s.add(card);
                        }
                        i++;
                    }
                    /*if (s.contains(null)) {
                        continue;
                    }*/
                    PokerHand candidate = new PokerHand(s);
                    if (best == null || candidate.compareTo(best) > 0) {
                        best = candidate;
                    }
                }
            }
        }
        return best;
    }

    // Simulates 10000 random Texas Hold Em runouts between two sets of hole cards
    public static Map<Integer, Double> simHoldEm(List<Set<Card>> holeCards) {
        return simHoldEm(holeCards, 10000, false);
    }

    public static Map<Integer, Double> simHoldEm(List<Set<Card>> holeCards, int trials, boolean printHands) {
        Map<Integer, Double> results = new TreeMap<>();
        for (int i=0; i<holeCards.size(); i++) {
            results.put(i,0.0);
        }
        for (int i=0; i<trials; i++) {
            List<Integer> winners = randomHoldEm(holeCards, printHands);
            for (int player : winners) {
                results.put(player, results.get(player) + 1.0/winners.size());
            }
        }
        return results;
    }

    public static Map<String,Integer> playAllHands(Set<Card> hole1, Set<Card> hole2) {
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

    public static List<Integer> randomHoldEm(List<Set<Card>> holeCards, boolean printHands) {
        Set<Card> allCards = new TreeSet<>();
        for (Set<Card> s : holeCards) {
            if (s.size() != 2) {
                throw new IllegalArgumentException("All players must have two hole cards");
            }
            for (Card c : s) {
                allCards.add(c);
            }
        }

        if (allCards.size() != 2*holeCards.size()) {
            throw new IllegalArgumentException("All players must have different hole cards");
        }

        List<Set<Card>> playerHands = new ArrayList<>();

        for (Set<Card> s : holeCards) {
            playerHands.add(new TreeSet<Card>(s));
        }

        while(allCards.size() < 2*holeCards.size() + 5) {
            Card card = randomCard();
            if (!allCards.contains(card)) {
                allCards.add(card);
                for (Set<Card> s : playerHands) {
                    s.add(card);
                }
                if (printHands) {System.out.print(card + " ");}
            }
        }
        if (printHands) {System.out.print("    ");}
        List<Integer> potWinners = new ArrayList<>();
        potWinners.add(0);

        for (int i=1; i<playerHands.size(); i++) {
            int num = holdEmBest(playerHands.get(i)).compareTo(holdEmBest(playerHands.get(potWinners.get(0))));
            if (num > 0) {
                potWinners.clear();
                potWinners.add(i);
            }
            else if (num == 0) {
                potWinners.add(i);
            }
        }

        return potWinners;
    }

    public static Card randomCard() {
        Random r = new Random();
        int x = 8 + r.nextInt(52);
        return new Card(x/4, x%4);
    }
}

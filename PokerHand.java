import java.util.*;

public class PokerHand implements Comparable<PokerHand>{
    public static final int SPADES = 0;
    public static final int HEARTS = 1;
    public static final int DIAMONDS = 2;
    public static final int CLUBS = 3;

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

    public static void main(String[] args) {
        Map<String,Integer> results = new TreeMap<>();
        results.put("Player 1 wins", 0);
        results.put("Splits", 0);
        results.put("Player 2 wins", 0);
        for (int t = 0; t < 100000; t++) {
            Set<Card> allCards = new HashSet<>();
            // Player 1 cards
            Card p11 = new Card(A,SPADES);
            Card p12 = new Card(Q,SPADES);

            Card p21 = p11;
            Card p22 = p12;

            while (p21.equals(p11) || p21.equals(p12)) {
                p21 = randomCard();
            }

            while (p22.equals(p11) || p22.equals(p12) || p22.equals(p21)) {
                p22 = randomCard();
            }

            allCards.add(p11);
            allCards.add(p12);
            allCards.add(p21);
            allCards.add(p22);

            while (allCards.size() < 9) {
                allCards.add(randomCard());
            }

            Set<Card> p1Cards = new HashSet<>();
            Set<Card> p2Cards = new HashSet<>();

            for (Card card : allCards) {
                if (card != p11 && card != p12) {
                    p2Cards.add(card);
                }
                if (card != p21 && card != p22) {
                    p1Cards.add(card);
                }
                
            }
            PokerHand p1BestHand = holdEmBest(p1Cards);
            PokerHand p2BestHand = holdEmBest(p2Cards);

            int result = p1BestHand.compareTo(p2BestHand);

            if (result > 0) {
                results.put("Player 1 wins", results.get("Player 1 wins") + 1);
            }
            else if (result == 0) {
                results.put("Splits", results.get("Splits") + 1);
            }
            else {
                results.put("Player 2 wins", results.get("Player 2 wins") + 1);
            }
        }
        System.out.println(results);
    }

    public static PokerHand holdEmBest(Set<Card> hand) {
        if (hand.size() != 7) {
            throw new IllegalArgumentException();
        }
        // returns a set consisting of the best five hand card
        PokerHand best = null;
        for (int a=0; a<7; a++) {
            for (int b=a+1; b<7; b++) {
                Set<Card> s = new HashSet<>();
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

    public static Card randomCard() {
        Random r = new Random();
        int x = 8 + r.nextInt(52);
        return new Card(x/4, x%4);
    }
}

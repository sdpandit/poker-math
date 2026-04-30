import java.util.*;

public class PokerHand implements Comparable<PokerHand>{

    public Set<Card> hand;
    private Map<Integer,Integer> ranks;
    private Map<Integer,Integer> suits;
    private int ranking;

    public PokerHand(Set<Card> hand) {
        if (hand.size() != 5) {
            throw new IllegalArgumentException();
        }
        this.hand = hand;
        this.ranks = new TreeMap<>();
        this.suits = new TreeMap<>();
        for (Card c : hand) {
            ranks.putIfAbsent(c.rank, 0);
            suits.putIfAbsent(c.suit, 0);
            ranks.put(c.rank,ranks.get(c.rank)+1);
            suits.put(c.suit,suits.get(c.suit)+1);
        }
        this.ranking = computeRanking();
    }

    private boolean hasFlush() {
        return suits.size() == 1;
    }

    private boolean hasStraight() {
        if (ranks.size() == 5 && setMax(ranks.keySet()) - setMin(ranks.keySet()) == 4) {
            return true;
        }
        // check for wheel
        if (ranks.containsKey(14) && ranks.containsKey(2) && ranks.containsKey(3)
            && ranks.containsKey(4) && ranks.containsKey(5)) {
            return true;
        }
        return false;
    }

    // returns the rank of a hand
    public int handRanking() {
        return this.ranking;
    }

    // computes the rank of a hand
    private int computeRanking() {
        boolean straight = hasStraight();
        boolean flush = hasFlush();
        if (straight && flush && setMin(ranks.keySet()) == 10) {
            // royal flush
            return 9;
        }
        if (straight && flush) {
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
        if (flush) {
            // flush
            return 5;
        }
        if (straight) {
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
            // high card
            return 0;
        }
    }
    
    private int setMin(Collection<Integer> s) {
        int min = Integer.MAX_VALUE;
        for (int x : s) {
            min = Math.min(x, min);
        }
        return min;
    }

    private int setMax(Collection<Integer> s) {
        int max = Integer.MIN_VALUE;
        for (int x : s) {
            max = Math.max(x, max);
        }
        return max;
    }

    @Override
    public int compareTo(PokerHand other) {
        if (other == null) {
            return 1;
        }
        int r1 = this.ranking;
        int r2 = other.ranking;
        if (r1 != r2) {
            return r1 - r2;
        }
        PriorityQueue<Pair> thisQueue = this.handQueue();
        PriorityQueue<Pair> otherQueue = other.handQueue();
        while (!thisQueue.isEmpty() && !otherQueue.isEmpty()) {
            Pair a = thisQueue.remove();
            Pair b = otherQueue.remove();
            if (a.rank != b.rank) {
                return a.rank - b.rank;
            }
        }
        return 0;
    }

    private PriorityQueue<Pair> handQueue() {
        PriorityQueue<Pair> pq = new PriorityQueue<>();
        int hr = this.handRanking();
        for (int rank : this.ranks.keySet()) {
            // handle wheel
            if (rank == 14 && (hr == 4 || hr == 8) && this.ranks.containsKey(5)) {
                pq.add(new Pair(1,1));
            } else {
                pq.add(new Pair(rank, this.ranks.get(rank)));
            }
        }
        return pq;
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

    public String toString() {
        String output = "";
        for (Card card : hand) {
            output = output + card.toString() + " ";
        }
        return output;
    }
}

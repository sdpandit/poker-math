import java.util.*;

public class PokerHand implements Comparable<PokerHand>{

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
        if (ranks.containsKey(14) && ranks.containsKey(2) && ranks.containsKey(3)
        && ranks.containsKey(4) && ranks.containsKey(5)) {
            return true;
        }
        return false;
    }

    // returns the rank of a hand
    public int handRank() {
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

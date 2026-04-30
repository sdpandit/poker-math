public class Card implements Comparable<Card> {

    private static final String[] RANKS = 
        {"", "", "2", "3", "4", "5", "6",
        "7", "8", "9", "T", "J", "Q", "K", "A"};
    private static final String[] SUITS = {"c", "d", "h", "s"};

    public int rank;
    public int suit;

    public Card(int rank, int suit) {
        if (rank < 2 || rank > 14 || suit < 0 || suit > 3) {
            throw new IllegalArgumentException();
        }
        this.rank = rank;
        this.suit = suit;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Card)) return false;
        Card c = (Card) other;
        return this.rank == c.rank
            && this.suit == c.suit;
    }

    public int compareTo(Card other) {
        // i hope this doesn't break anything
        return this.hashCode() - other.hashCode();
    }

    @Override
    public int hashCode() {
        return 4*rank + suit;
    }

    public String toString() {
        return RANKS[rank] + SUITS[suit];
    }
}
public class Card implements Comparable<Card>{
    public static final int SPADES = 3;
    public static final int HEARTS = 2;
    public static final int DIAMONDS = 1;
    public static final int CLUBS = 0;

    public static final int T = 10;
    public static final int J = 11;
    public static final int Q = 12;
    public static final int K = 13;
    public static final int A = 14;

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
        if (this == other) {return true;}
        if (!(other instanceof Card)) {return false;}
        return this.rank == ((Card) other).rank && this.suit == ((Card) other).suit;
    }

    public int compareTo(Card other) {
        if (this.rank != other.rank) {
            return this.rank-other.rank;
        }
        return this.suit-other.suit;
    }

    @Override
    public int hashCode() {
        return 4*rank + suit;
    }

    public String toString() {
        String output = "";
        if (rank <= 9) {
            output = output + rank;
        }
        else if (rank == 10) {output = output + "T";}
        else if (rank == J) {output = output + "J";}
        else if (rank == Q) {output = output + "Q";}
        else if (rank == K) {output = output + "K";}
        else if (rank == A) {output = output + "A";}

        if (suit == SPADES) {output = output + "S";}
        if (suit == HEARTS) {output = output + "H";}
        if (suit == DIAMONDS) {output = output + "D";}
        if (suit == CLUBS) {output = output + "C";}

        return output;
    }
}
public class Card implements Comparable<Card>{

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
        else if (rank == 11) {output = output + "J";}
        else if (rank == 12) {output = output + "Q";}
        else if (rank == 13) {output = output + "K";}
        else if (rank == 13) {output = output + "A";}

        if (suit == 3) {output = output + "S";}
        if (suit == 2) {output = output + "H";}
        if (suit == 1) {output = output + "D";}
        if (suit == 0) {output = output + "C";}

        return output;
    }
}
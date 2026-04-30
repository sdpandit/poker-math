import java.util.*;

public class Deck {
    private List<Card> deck;
    private Random r;

    public Deck() {
        this.deck = new ArrayList<>();
        for (int i=8; i<60; i++) {
            deck.add(new Card(i/4, i%4));
        }
        r = new Random();
    }

    public int deckSize() {
        return deck.size();
    }

    public Card dealRandomCard() {
        return deck.remove(r.nextInt(deck.size()));
    }

    public Card removeCard(int rank, int suit) {
        return this.removeCard(new Card(rank, suit));
    }

    public Card removeCard(Card c) {
        if (!deck.remove(c)) {
            return null;
        }
        return c;
    }

    public void removeAll(Set<Card> s) {
        for (Card c : s) {this.removeCard(c);}
    }

    public Card getCardAtPosition(int i) {
        if (i >= this.deckSize()) {
            throw new IllegalArgumentException("Not enough cards");
        }
        return deck.get(i);
    }

    public void reset() {
        deck.clear();
        for (int i=8; i<60; i++) {
            deck.add(new Card(i/4, i%4));
        }
    }
}
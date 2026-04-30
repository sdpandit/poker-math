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
        if (deck.isEmpty()) {
            throw new NoSuchElementException("No cards left in the deck");
        }
        return deck.remove(r.nextInt(this.deckSize()));
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
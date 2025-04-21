import java.util.*;

public class Deck {
    private List<Card> remainingCards;
    private Random r;

    public Deck() {
        this.remainingCards = new ArrayList<>();
        for (int i=8; i<60; i++) {
            remainingCards.add(new Card(i/4, i%4));
        }
        r = new Random();
    }

    public int deckSize() {
        return remainingCards.size();
    }

    public Card dealRandomCard() {
        if (remainingCards.size() == 0) {
            throw new NoSuchElementException("No cards left in the deck");
        }
        return remainingCards.remove(r.nextInt(this.deckSize()));
    }

    public Card removeCard(int rank, int suit) {
        return this.removeCard(new Card(rank, suit));
    }

    public Card removeCard(Card c) {
        if (!remainingCards.remove(c)) {
            return null;
        }
        return c;
    }

    public Card getCardAtPosition(int i) {
        if (i >= this.deckSize()) {
            throw new IllegalArgumentException("Not enough cards");
        }
        return remainingCards.get(i);
    }

    public void reset() {
        remainingCards.clear();
        for (int i=8; i<60; i++) {
            remainingCards.add(new Card(i/4, i%4));
        }
    }
}
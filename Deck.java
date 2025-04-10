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

    public Card drawRandomCard() {
        if (remainingCards.size() == 0) {
            throw new NoSuchElementException("No cards left in the deck");
        }
        return remainingCards.remove(r.nextInt(this.deckSize()));
    }

    public boolean removeCard(Card c) {
        return remainingCards.remove(c);
    }
}
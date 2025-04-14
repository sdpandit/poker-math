import java.util.*;

public class Player {
    public String name;
    private Set<Card> hand;
    public int stack;
    public boolean isActive;

    public Player(String name, int stack) {
        this.name = name;
        this.stack = stack;
        this.isActive = false;
        this.hand = new TreeSet<>();
    }

    public void dealNewHand(Deck deck) {
        hand.clear();
        hand.add(deck.dealRandomCard());
        hand.add(deck.dealRandomCard());
    }
}

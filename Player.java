import java.util.*;

public class Player {
    public String name;
    private Set<Card> hand;
    public int stack;
    public int currBet;
    private boolean active;
    public boolean isAllIn;

    public Player(String name, int stack) {
        this.name = name;
        this.stack = stack;
        this.currBet = 0;
        this.active = false;
        this.hand = new TreeSet<>();
    }

    public void dealNewHand(Deck deck) {
        hand.clear();
        hand.add(deck.dealRandomCard());
        hand.add(deck.dealRandomCard());
    }

    public Set<Card> getHand() {
        return new TreeSet<>(hand);
    }

    public void bet(int wager) {
        if (wager > stack) {
            throw new IllegalStateException("Not enough chips in stack");
        }
        this.currBet += wager;
        this.stack -= wager;
    }

    public void fold() {
        this.active = false;
    }

    public boolean isActive() {
        return this.active;
    }
}

import java.util.*;

public class Game {
    private List<Player> players;
    private List<Integer> activePlayers;
    private int maxPlayers;
    private int bigBlind;
    private int pot;
    private int dealer;
    private int lastRaiser;
    private int currPlayer;
    private int currBet;
    private Set<Card> community;
    private Deck deck;

    public Game(int maxPlayers, int bigBlind) {
        this.players = new ArrayList<>();
        this.activePlayers = new ArrayList<>();
        this.maxPlayers = maxPlayers;
        this.bigBlind = bigBlind;
        this.pot = 0;
        this.dealer = 0;
        this.lastRaiser = 0;
        this.currPlayer = 0;
        this.currBet = 0;
        this.community = new TreeSet<>();
        this.deck = new Deck();
    }

    public void addPlayer(String name, int stack) {
        if (this.players.size() == this.maxPlayers) {
            throw new IllegalStateException("No seat available");
        }
        Player newPlayer = new Player(name, stack);
        this.players.add(newPlayer);
    }

    public void resetNewHand() {
        pot = 0;
        dealer = (dealer + 1) % players.size();
        community.clear();
        activePlayers.clear();
        deck.reset();
    }

    public void bettingRound() {
        // play a betting round
        if (community.size() == 0) {
            // pre-flop
        }
        lastRaiser = (dealer + 1) % players.size();
        currPlayer = lastRaiser;
        do {
            if (players.get(currPlayer).isActive()) {
                // Options to CHECK, CALL, RAISE, FOLD
                // CHECK: only if currBet is 0
                // CALL: bet currBet
                // RAISE: raise by at least lastRaise, set lastRaiser
                // FOLD: player goes inactive, remove from activePlayers
                if (activePlayers.size() == 1) {
                    return;
                }
            }
            currPlayer = (currPlayer + 1) % players.size();
        } while (currPlayer != lastRaiser);
    }

    public void showdown() {
        List<Set<Card>> playerHands = new ArrayList<>();
        for (int i : activePlayers) {
            playerHands.add(players.get(i).getHand());
        }
        List<Integer> potWinners = PokerMath.holdEmWinners(playerHands, community);
        for (int j : potWinners) {
            players.get(activePlayers.get(j)).stack += pot/potWinners.size();
        }
    }

    public void playHand() {
        if (players.size() < 2) {
            throw new IllegalStateException("Need at least two players");
        }
        while (true) {
            resetNewHand();
            // blinds, betting round

            // flop
            community.add(deck.dealRandomCard());
            community.add(deck.dealRandomCard());
            community.add(deck.dealRandomCard());
            bettingRound();
            if (activePlayers.size() == 1) {break;}

            // turn
            community.add(deck.dealRandomCard());
            bettingRound();
            if (activePlayers.size() == 1) {break;}

            // river
            community.add(deck.dealRandomCard());
            bettingRound();
            if (activePlayers.size() == 1) {break;}

            showdown();
            break;
        }
    }
}

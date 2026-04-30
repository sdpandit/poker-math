public class PokerHand implements Comparable<PokerHand>{

    private Card[] hand;
    private int[] rankCount;
    private int[] suitCount;
    private int ranking;
    private boolean wheel;

    public PokerHand(Card[] hand) {
        if (hand.length != 5) {
            throw new IllegalArgumentException();
        }
        this.hand = hand;
        this.rankCount = new int[15];
        this.suitCount = new int[4];
        for (Card c : hand) {
            rankCount[c.rank]++;
            suitCount[c.suit]++;
        }
        this.wheel = (rankCount[14] == 1 && rankCount[2] == 1
            && rankCount[3] == 1 && rankCount[4] == 1 && rankCount[5] == 1);
        this.ranking = computeRanking();
    }

    private boolean hasFlush() {
        return maxOcc(suitCount) == 5;
    }

    private boolean hasStraight() {
        if (wheel) return true;
        if (maxOcc(rankCount) == 1
            && this.maxRank() - this.minRank() == 4) {
            return true;
        }
        return false;
    }

    // returns the rank of a hand
    public int handRanking() {
        return this.ranking;
    }

    // computes the rank of a hand
    private int computeRanking() {
        boolean straight = hasStraight();
        boolean flush = hasFlush();
        int maxCount = maxOcc(rankCount);
        int nonzero = countNonzero(rankCount);
        if (straight && flush) {
            // royal or straight flush
            return 8;
        } else if (maxCount == 4) {
            // quads
            return 7;
        } else if (maxCount == 3 && nonzero == 2) {
            // full house
            return 6;
        } else if (flush) {
            // flush
            return 5;
        } else if (straight) {
            // straight
            return 4;
        } else if (maxCount == 3) {
            // trips
            return 3;
        } else if (maxCount == 2 && nonzero == 3) {
            // two pair
            return 2;
        } else if (maxCount == 2) {
            // pair
            return 1;
        } else {
            // high card
            return 0;
        }
    }

    private int countNonzero(int[] arr) {
        int ret = 0;
        for (int num : arr) {
            if (num > 0) {
                ret++;
            }
        }
        return ret;
    }

    private int maxOcc(int[] arr) {
        int max = 0;
        for (int x : arr) {
            if (x > 0) {
                max = Math.max(x, max);
            }
        }
        return max;
    }

    private int minRank() {
        for (int i=0; i<rankCount.length; i++) {
            if (rankCount[i] > 0) {
                return i;
            }
        }
        return -1;
    }

    private int maxRank() {
        for (int i = rankCount.length-1; i>=0; i--) {
            if (rankCount[i] > 0) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int compareTo(PokerHand other) {
        if (other == null) {
            return 1;
        }
        if (this.ranking != other.ranking) {
            return this.ranking - other.ranking;
        } else if (this.ranking == 8 || this.ranking == 4) {
            if (this.wheel && !other.wheel) {
                return -1;
            } else if (!other.wheel && this.wheel) {
                return 1;
            }
        }
        return this.tiebreak(other);
    }

    // helper method for breaking ties
    public int tiebreak(PokerHand other) {
        for (int i = 3; i >= 1; i--) {
            for (int j = 14; j >= 0; j--) {
                if (this.rankCount[j] >= i && other.rankCount[j] < i) {
                    return 1;
                }
                if (other.rankCount[j] >= i && this.rankCount[j] < i) {
                    return -1;
                }
            }
        }
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Card c : hand) {
            sb.append(c).append(" ");
        }
        return sb.toString();
    }
}

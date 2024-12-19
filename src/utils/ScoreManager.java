package utils;

public class ScoreManager {
    private int score;
    private int xp;

    public void addScore(int amount) {
        score += amount;
    }

    public void addXP(int amount) {
        xp += amount;
    }

    public int getScore() {
        return score;
    }

    public int getXP() {
        return xp;
    }
}

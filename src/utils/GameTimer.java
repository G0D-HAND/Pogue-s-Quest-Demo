package utils;

public class GameTimer {
    private long lastTime;
    private final long interval;

    public GameTimer(long interval) {
        this.interval = interval;
        this.lastTime = System.currentTimeMillis();
    }

    public boolean hasElapsed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime >= interval) {
            lastTime = currentTime;
            return true;
        }
        return false;
    }
}

package sample;

/**
 * Created by Komputer on 6/22/2017.
 */
public class NanoTimer {
    private long startTime;

    public NanoTimer() {
        startTime = System.nanoTime();
    }

    public NanoTimer(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getElapsedTimeNano() {
        return startTime - System.nanoTime();
    }

    public double getElapsedTimeSec() {
        return (System.nanoTime() - startTime) / 10e9;
    }
}

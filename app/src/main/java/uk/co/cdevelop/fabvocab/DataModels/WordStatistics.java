package uk.co.cdevelop.fabvocab.DataModels;

/**
 * Created by Chris on 12/03/2017.
 */
public class WordStatistics {
    private int avgRecall;
    private int avgFluency;
    private int bestRecall;
    private int bestFluency;
    private int lastRecall;
    private int lastFluency;

    public WordStatistics(int avgRecall, int avgFluency, int bestRecall, int bestFluency, int lastRecall, int lastFluency) {
        this.avgRecall = avgRecall;
        this.avgFluency = avgFluency;
        this.bestRecall = bestRecall;
        this.bestFluency = bestFluency;
        this.lastRecall = lastRecall;
        this.lastFluency = lastFluency;
    }

    public int getAvgRecall() {
        return avgRecall;
    }

    public int getAvgFluency() {
        return avgFluency;
    }

    public int getBestRecall() {
        return bestRecall;
    }

    public int getBestFluency() {
        return bestFluency;
    }

    public int getLastRecall() {
        return lastRecall;
    }

    public int getLastFluency() {
        return lastFluency;
    }
}

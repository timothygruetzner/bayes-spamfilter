package ch.fhnw.dist.spamfilter.service;

public class Prediction {

    public enum PredictionType {
        SPAM,
        HAM
    }

    private PredictionType type;
    private double probability;

    public Prediction(PredictionType type, double probability) {
        this.type = type;
        this.probability = probability;
    }

    public PredictionType getType() {
        return type;
    }

    public void setType(PredictionType type) {
        this.type = type;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }
}



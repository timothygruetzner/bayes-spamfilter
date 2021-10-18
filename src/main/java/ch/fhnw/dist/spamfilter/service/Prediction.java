package ch.fhnw.dist.spamfilter.service;

public interface Prediction {
    PredictionType getType();
    double getProbability();

    enum PredictionType {
        SPAM,
        HAM
    }
}



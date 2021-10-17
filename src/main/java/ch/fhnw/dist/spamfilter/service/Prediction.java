package ch.fhnw.dist.spamfilter.service;

public interface Prediction {
    PredictionType getType();
    double getPrediction();
}

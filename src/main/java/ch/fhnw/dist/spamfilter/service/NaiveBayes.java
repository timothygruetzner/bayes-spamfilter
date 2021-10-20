package ch.fhnw.dist.spamfilter.service;

public interface NaiveBayes {
    void trainMany(String[][] spamTrainingSet, String[][] hamTrainingSet);

    void train(String[] words, Prediction.PredictionType type);

    Prediction predict(String[] content);
}

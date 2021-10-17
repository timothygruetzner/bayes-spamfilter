package ch.fhnw.dist.spamfilter.service;

public interface NaiveBayes {
    void train(String[][] spamTrainingSet, String[][] hamTrainingSet);

    void reinforce(String[][] spamReinforceSet, String[][] hamReinforceSet);

    Prediction predict(String[] content);
}

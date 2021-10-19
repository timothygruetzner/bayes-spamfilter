package ch.fhnw.dist.spamfilter.service.impls;

import ch.fhnw.dist.spamfilter.service.NaiveBayes;
import ch.fhnw.dist.spamfilter.service.Prediction;

import java.util.*;
import java.util.stream.Collectors;

public class NaiveBayesImpl implements NaiveBayes {
    public final static double ALPHA = 0.00000000001;
    public final static double THRESHOLD = 1;
    public final static double BIAS = 0.5;

    Map<String, Double> spamWordCountPerMail = new HashMap<>();
    Map<String, Double> hamWordCountPerMail = new HashMap<>();

    private int nSpamMails = 0;
    private int nHamMails = 0;

    @Override
    public void trainMany(String[][] spamTrainingSet, String[][] hamTrainingSet) {
        for (String[] words : spamTrainingSet) {
            train(words, Prediction.PredictionType.SPAM);
        }

        for (String[] words : hamTrainingSet) {
            train(words, Prediction.PredictionType.HAM);
        }
    }

    @Override
    public void reinforce(String[][] spamReinforceSet, String[][] hamReinforceSet) {

    }

    @Override
    public Prediction predict(String[] content) {
        double probabilityOfSpam = calculateProbability(content);

        if (probabilityOfSpam >= THRESHOLD) {
            return new Prediction(Prediction.PredictionType.SPAM, probabilityOfSpam);
        } else {
            return new Prediction(Prediction.PredictionType.HAM, probabilityOfSpam);
        }
    }

    @Override
    public void train(String[] words, Prediction.PredictionType type) {
        Map<String, Double> wordCountMap = getMap(type);
        Set<String> uniqueWords = Arrays.stream(words).collect(Collectors.toSet());
        uniqueWords.forEach(word -> wordCountMap.compute(word, (ignore, value) -> Optional.ofNullable(value).orElse(0.0) + 1.0));
        increaseMailCount(type);
    }

    private void increaseMailCount(Prediction.PredictionType type) {
        if (type == Prediction.PredictionType.SPAM) {
            nSpamMails += 1;
        } else {
            nHamMails += 1;
        }
    }

    private Map<String, Double> getMap(Prediction.PredictionType type) {
        if (type == Prediction.PredictionType.SPAM) {
            return spamWordCountPerMail;
        } else {
            return hamWordCountPerMail;
        }
    }

    private double calculateProbability(String[] words) {
        Double summed = Arrays.stream(words)
                .filter(word -> spamWordCountPerMail.containsKey(word) || hamWordCountPerMail.containsKey(word))
                .map(word -> {
                    double nSpam = spamWordCountPerMail.getOrDefault(word, ALPHA);
                    double nHam = hamWordCountPerMail.getOrDefault(word, ALPHA);
                    double spamProbability = nSpam / nSpamMails;
                    double hamProbability = nHam / nHamMails;

                    return Math.log(hamProbability) - Math.log(spamProbability);
                })
                .reduce(0.0, Double::sum);
        return 1 / (1 + Math.exp(summed));
    }
}

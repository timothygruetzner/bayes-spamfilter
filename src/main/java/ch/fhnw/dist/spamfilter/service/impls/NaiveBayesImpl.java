package ch.fhnw.dist.spamfilter.service.impls;

import ch.fhnw.dist.spamfilter.service.NaiveBayes;
import ch.fhnw.dist.spamfilter.service.Prediction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NaiveBayesImpl implements NaiveBayes {
    private final static double α = 0.01;
    private final static double THRESHOLD = 0.1;

    Map<String, Double> spamProbabilities = new HashMap<>();
    Map<String, Double> hamProbabilities = new HashMap<>();

    @Override
    public void train(String[][] spamTrainingSet, String[][] hamTrainingSet) {
        spamProbabilities = calculateProbabilities(spamTrainingSet);
        hamProbabilities = calculateProbabilities(hamTrainingSet);
    }

    @Override
    public void reinforce(String[][] spamReinforceSet, String[][] hamReinforceSet) {

    }

    @Override
    public Prediction predict(String[] content) {
        return null;
    }

    private Map<String, Double> calculateProbabilities(String[][] wordsInFiles) {
        String[] words = Arrays.stream(wordsInFiles).flatMap(Arrays::stream).toArray(String[]::new);
        int nWords = words.length;

        return Arrays.stream(words)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().doubleValue() / nWords))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

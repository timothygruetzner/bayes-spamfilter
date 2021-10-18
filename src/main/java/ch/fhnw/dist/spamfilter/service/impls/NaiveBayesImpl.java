package ch.fhnw.dist.spamfilter.service.impls;

import ch.fhnw.dist.spamfilter.service.NaiveBayes;
import ch.fhnw.dist.spamfilter.service.Prediction;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NaiveBayesImpl implements NaiveBayes {
    public final static double ALPHA = 1;
    public final static double THRESHOLD = 0.00001;
    public final static double BIAS = 0.5;

    Map<String, Double> spamProbabilities = new HashMap<>();
    Map<String, Double> hamProbabilities = new HashMap<>();

    @Override
    public void train(String[][] spamTrainingSet, String[][] hamTrainingSet) {
        spamProbabilities = calculateProbabilities(spamTrainingSet);
        hamProbabilities = calculateProbabilities(hamTrainingSet);

        insertNonExistent(spamProbabilities.keySet(), hamProbabilities.keySet(), hamProbabilities);
        insertNonExistent(hamProbabilities.keySet(), spamProbabilities.keySet(), spamProbabilities);
    }

    @Override
    public void reinforce(String[][] spamReinforceSet, String[][] hamReinforceSet) {

    }

    @Override
    public Prediction predict(String[] content) {
        double probabilityOfSpam = calculateProbability(content, spamProbabilities) * BIAS;
        double probabilityOfHam = calculateProbability(content, hamProbabilities) * (1 - BIAS);

        System.out.println(probabilityOfSpam);
        System.out.println(probabilityOfHam);

        double spam = probabilityOfSpam / (probabilityOfSpam + probabilityOfHam);
        double ham = probabilityOfHam / (probabilityOfHam + probabilityOfSpam);

        if (Math.abs(spam - ham) > THRESHOLD) {
            return new Prediction(Prediction.PredictionType.SPAM, spam);
        } else {
            return new Prediction(Prediction.PredictionType.HAM, ham);
        }
    }

    private void insertNonExistent(Set<String> lookupSet, Set<String> intersectionSet, Map<String, Double> probabilitiesMap) {
        lookupSet.removeAll(intersectionSet);
        lookupSet.forEach(key -> probabilitiesMap.put(key, ALPHA));
    }

    private double calculateProbability(String[] words, Map<String, Double> probabilityOfWord) {
        return 1 / (1 + Math.pow(Math.E, Arrays.stream(words)
                .filter(probabilityOfWord::containsKey)
                .map(probabilityOfWord::get)
                .map(probability -> Math.log(1 - probability) - Math.log(probability))
                .reduce(1.0, (accumulated, current) -> accumulated * current)));
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

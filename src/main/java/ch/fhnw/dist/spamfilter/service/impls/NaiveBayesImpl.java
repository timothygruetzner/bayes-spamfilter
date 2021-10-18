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
    public final static double ALPHA = 0.000001;
    public final static double THRESHOLD = 0.1;
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
        double probabilityOfSpam = calculateProbability(content) * BIAS;
        System.out.println(probabilityOfSpam);

        if (probabilityOfSpam > THRESHOLD) {
            return new Prediction(Prediction.PredictionType.SPAM, probabilityOfSpam);
        } else {
            return new Prediction(Prediction.PredictionType.HAM, probabilityOfSpam);
        }
    }

    private void insertNonExistent(Set<String> lookupSet, Set<String> intersectionSet, Map<String, Double> probabilitiesMap) {
        lookupSet.removeAll(intersectionSet);
        lookupSet.forEach(key -> probabilitiesMap.put(key, ALPHA));
    }

    private double calculateProbability(String[] words) {
        return 1 / (1 + Math.exp(Arrays.stream(words)
                .filter(word -> spamProbabilities.containsKey(word) && hamProbabilities.containsKey(word))
                .map(word -> {
                    Double spamProbability = spamProbabilities.get(word);
                    Double hamProbability = hamProbabilities.get(word);

                    return Math.log(hamProbability) - Math.log(spamProbability);
                })
                .reduce(0.0, Double::sum)));
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

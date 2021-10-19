package ch.fhnw.dist.spamfilter;

import ch.fhnw.dist.spamfilter.service.NaiveBayes;
import ch.fhnw.dist.spamfilter.service.Parser;
import ch.fhnw.dist.spamfilter.service.Prediction;
import ch.fhnw.dist.spamfilter.service.impls.NaiveBayesImpl;
import ch.fhnw.dist.spamfilter.service.impls.WordParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {
    public static final String HAM_TRAINING_SET = "ham-anlern.zip";
    public static final String HAM_CALIBRATE_SET = "ham-kalibrierung.zip";
    public static final String SPAM_TRAINING_SET = "spam-anlern.zip";
    public static final String SPAM_CALIBRATE_SET = "spam-kalibrierung.zip";
    public static final String HAM_TEST_SET = "ham-test.zip";
    public static final String SPAM_TEST_SET = "spam-test.zip";

    private final Parser wordParser;
    private final NaiveBayes bayesImpl;

    public Main() {
        this.wordParser = new WordParser();
        this.bayesImpl = new NaiveBayesImpl();
    }

    public void run() throws IOException {
        System.out.println("*****************************************************************");
        System.out.println("*            Naive Bayes Spam Filter                            *");
        System.out.println("*            DIST HS 2021                                       *");
        System.out.println("*            Impl. by Nathanael Weber & Timothy Grützner        *");
        System.out.println("*****************************************************************");
        System.out.println();

        System.out.println("RUN CONFIGURATION:");
        System.out.printf("- Alpha:                          %f%n", NaiveBayesImpl.ALPHA);
        System.out.printf("- Spam Threshold (|P(H) - P(S)|): %f%n", NaiveBayesImpl.THRESHOLD);
        System.out.println();

        String[][] spamTrainingSet = getZipContents(SPAM_TRAINING_SET).toArray(new String[0][0]);
        String[][] hamTrainingSet = getZipContents(HAM_TRAINING_SET).toArray(new String[0][0]);

        this.bayesImpl.trainMany(spamTrainingSet, hamTrainingSet);
        System.out.printf("Trained with %d spam emails and %d ham emails%n%n", spamTrainingSet.length, hamTrainingSet.length);

        List<String[]> hamTestfiles = getZipContents(HAM_TEST_SET);
        System.out.printf("Testing with %d HAM files...", hamTestfiles.size());
        long correctHamGuesses = hamTestfiles.stream()
                .map(this.bayesImpl::predict)
                .filter(p -> p.getType() == Prediction.PredictionType.HAM)
                .count();
        System.out.printf("Guessed %d / %d HAM correctly (%f%%)%n", correctHamGuesses, hamTestfiles.size(), (double)correctHamGuesses/hamTestfiles.size()*100);

        List<String[]> spamTestfiles = getZipContents(SPAM_TEST_SET);
        System.out.printf("Testing with %d SPAM files...", spamTestfiles.size());
        long correctSpamGuesses = spamTestfiles.stream()
                .map(this.bayesImpl::predict)
                .filter(p -> p.getType() == Prediction.PredictionType.SPAM)
                .count();
        System.out.printf("Guessed %d / %d SPAM correctly (%f%%)%n", correctSpamGuesses, spamTestfiles.size(), (double)correctSpamGuesses/spamTestfiles.size()*100);


    }

    private List<String[]> getZipContents(String fileName) throws IOException {
        try (ZipFile zipFile = new ZipFile(getClass().getClassLoader().getResource(fileName).getFile())) {
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();

            List<String[]> content = new ArrayList<>();
            while (zipEntries.hasMoreElements()) {
                ZipEntry zipEntry = zipEntries.nextElement();
                try (InputStream stream = zipFile.getInputStream(zipEntry)) {
                    content.add(wordParser.getWords(stream));
                }
            }

            return content;
        }
    }


    public static void main(String[] args) throws IOException {
        new Main().run();
    }

}

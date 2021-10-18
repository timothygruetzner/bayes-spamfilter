package ch.fhnw.dist.spamfilter;

import ch.fhnw.dist.spamfilter.service.NaiveBayes;
import ch.fhnw.dist.spamfilter.service.Parser;
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

    private final Parser wordParser;
    private final NaiveBayes bayesImpl;

    public Main() {
        this.wordParser = new WordParser();
        this.bayesImpl = new NaiveBayesImpl();
    }

    public void run() throws IOException {
        this.bayesImpl.train(getZipContents(SPAM_TRAINING_SET).toArray(new String[0][0]), getZipContents(HAM_TRAINING_SET).toArray(new String[0][0]));
    }

    private List<String[]> getZipContents(String fileName) throws IOException {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(getClass().getClassLoader().getResource(fileName).getFile());
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();

            List<String[]> content = new ArrayList<>();
            while (zipEntries.hasMoreElements()) {
                ZipEntry zipEntry = zipEntries.nextElement();
                try (InputStream stream = zipFile.getInputStream(zipEntry)){
                    content.add(wordParser.getWords(stream));
                }
            }

            return content;
        } finally {
            if(zipFile != null) {
                zipFile.close();
            }
        }
    }


    public static void main(String[] args) throws IOException {
        new Main().run();
    }

}

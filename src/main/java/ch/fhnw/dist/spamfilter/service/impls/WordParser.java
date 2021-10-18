package ch.fhnw.dist.spamfilter.service.impls;

import ch.fhnw.dist.spamfilter.service.Parser;

import java.io.InputStream;

public class WordParser implements Parser {
    @Override
    public String[] getWords(InputStream fileStream) {
        return new String[0];
    }
}

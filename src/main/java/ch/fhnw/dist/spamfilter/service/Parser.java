package ch.fhnw.dist.spamfilter.service;

import java.io.InputStream;

public interface Parser {
    String[] getWords(InputStream fileStream);
}

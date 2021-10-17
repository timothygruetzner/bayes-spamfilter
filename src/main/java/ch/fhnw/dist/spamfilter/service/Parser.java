package ch.fhnw.dist.spamfilter.service;

public interface Parser {
    ParseResult parseContent(String content);

    String[] getWords(ParseResult result);
}

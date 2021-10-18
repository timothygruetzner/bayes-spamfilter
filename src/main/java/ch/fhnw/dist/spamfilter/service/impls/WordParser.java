package ch.fhnw.dist.spamfilter.service.impls;

import ch.fhnw.dist.spamfilter.service.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class WordParser implements Parser {
    @Override
    public String[] getWords(InputStream fileStream) throws IOException {
        return new String(fileStream.readAllBytes(), StandardCharsets.UTF_8)
                .split("\\s"); //split by whitespace (" ", \n, \t, etc.)
    }
}

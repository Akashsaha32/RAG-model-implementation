package com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TextSplitter {
    private static final Logger logger = LoggerFactory.getLogger(TextSplitter.class);
    private static final Pattern SENTENCE_PATTERN = Pattern.compile("(?<=[.!?])\\s+");

    public List<String> splitIntoChunks(String text, int chunkSize) {
        logger.debug("Splitting text into chunks of size {}", chunkSize);
        List<String> chunks = new ArrayList<>();

        // First try to split by sentences
        String[] sentences = SENTENCE_PATTERN.split(text);
        StringBuilder currentChunk = new StringBuilder();

        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() > chunkSize && currentChunk.length() > 0) {
                chunks.add(currentChunk.toString());
                currentChunk = new StringBuilder();
            }
            currentChunk.append(sentence).append(" ");
        }

        // Add the last chunk if not empty
        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString());
        }

        logger.debug("Created {} chunks", chunks.size());
        return chunks;
    }
}
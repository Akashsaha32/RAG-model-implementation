package com;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class InMemoryVectorStore implements VectorStore {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryVectorStore.class);

    private final List<DocumentVector> documentVectors = new ArrayList<>();

    public void addDocument(String text, float[] embedding) {
        documentVectors.add(new DocumentVector(text, embedding));
        logger.debug("Added document to vector store. Total documents: {}", documentVectors.size());
    }

    public List<String> findSimilarDocuments(float[] queryEmbedding, int k) {
        logger.debug("Finding {} most similar documents", k);

        // Use a priority queue to keep track of top k documents
        PriorityQueue<DocumentSimilarity> heap = new PriorityQueue<>(
                Comparator.comparingDouble(DocumentSimilarity::getSimilarity)
        );

        for (DocumentVector doc : documentVectors) {
            float similarity = EmbeddingGenerator.cosineSimilarity(queryEmbedding, doc.embedding);

            if (heap.size() < k) {
                heap.offer(new DocumentSimilarity(doc.text, similarity));
            } else if (similarity > heap.peek().similarity) {
                heap.poll();
                heap.offer(new DocumentSimilarity(doc.text, similarity));
            }
        }

        // Extract results in order of similarity
        List<String> results = new ArrayList<>();
        while (!heap.isEmpty()) {
            results.add(0, heap.poll().text); // Add to beginning to reverse order
        }

        logger.debug("Found {} similar documents", results.size());
        return results;
    }

    private static class DocumentVector {
        final String text;
        final float[] embedding;

        DocumentVector(String text, float[] embedding) {
            this.text = text;
            this.embedding = embedding;
        }
    }

    private static class DocumentSimilarity {
        final String text;
        final float similarity;

        DocumentSimilarity(String text, float similarity) {
            this.text = text;
            this.similarity = similarity;
        }

        float getSimilarity() {
            return similarity;
        }
    }
}
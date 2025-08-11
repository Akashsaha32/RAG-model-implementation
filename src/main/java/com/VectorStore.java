package com;

import java.util.List;

public interface VectorStore {
    void addDocument(String text, float[] embedding);
    List<String> findSimilarDocuments(float[] queryEmbedding, int k);
}
package com;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class EmbeddingGenerator {
    private static final Logger logger = LoggerFactory.getLogger(EmbeddingGenerator.class);
    private static final Map<String, float[]> EMBEDDING_CACHE = new HashMap<>();

    public static float[] generateEmbedding(String text) {
        // Check cache first
        if (EMBEDDING_CACHE.containsKey(text)) {
            logger.debug("Using cached embedding for text");
            return EMBEDDING_CACHE.get(text);
        }

        logger.debug("Generating new embedding for text: {}", text.substring(0, Math.min(50, text.length())));

        // Simulate embedding generation (replace with actual API call)
        float[] embedding = new float[384]; // Typical small embedding size
        for (int i = 0; i < embedding.length; i++) {
            embedding[i] = (float) (Math.random() * 2 - 1); // Random values between -1 and 1
        }

        // Normalize the vector
        float[] normalizedEmbedding = normalizeVector(embedding);
        EMBEDDING_CACHE.put(text, normalizedEmbedding);
        return normalizedEmbedding;
    }

    public static float cosineSimilarity(float[] vec1, float[] vec2) {
        // Convert float arrays to double arrays
        double[] dVec1 = toDoubleArray(vec1);
        double[] dVec2 = toDoubleArray(vec2);

        RealVector v1 = new ArrayRealVector(dVec1);
        RealVector v2 = new ArrayRealVector(dVec2);
        return (float) v1.cosine(v2);
    }

    private static float[] normalizeVector(float[] vector) {
        // Convert to double[] for normalization
        double[] dVector = toDoubleArray(vector);
        RealVector realVector = new ArrayRealVector(dVector);
        RealVector normalized = realVector.unitVector();

        // Convert back to float[]
        return toFloatArray(normalized.toArray());
    }

    private static double[] toDoubleArray(float[] floatArray) {
        double[] doubleArray = new double[floatArray.length];
        for (int i = 0; i < floatArray.length; i++) {
            doubleArray[i] = floatArray[i];
        }
        return doubleArray;
    }

    private static float[] toFloatArray(double[] doubleArray) {
        float[] floatArray = new float[doubleArray.length];
        for (int i = 0; i < doubleArray.length; i++) {
            floatArray[i] = (float) doubleArray[i];
        }
        return floatArray;
    }
}
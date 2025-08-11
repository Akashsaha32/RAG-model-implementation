package com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Scanner;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String DEEPSEEK_API_KEY = "sk-2daad37f17704dedb96c51a75004b77d";

    public static void main(String[] args) {
        try {
            // Initialize components
            PDFProcessor pdfProcessor = new PDFProcessor();
            TextSplitter textSplitter = new TextSplitter();
            VectorStore vectorStore = new InMemoryVectorStore();
            DeepSeekClient deepSeekClient = new DeepSeekClient(DEEPSEEK_API_KEY);

            // Load and process PDF
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter path to PDF file: ");
            String pdfPath = scanner.nextLine();

            logger.info("Processing PDF file: {}", pdfPath);
            String fullText = pdfProcessor.extractTextFromPDF(pdfPath);
            List<String> chunks = textSplitter.splitIntoChunks(fullText, 500);

            logger.info("Generating embeddings for {} chunks", chunks.size());
            for (String chunk : chunks) {
                float[] embedding = EmbeddingGenerator.generateEmbedding(chunk);
                vectorStore.addDocument(chunk, embedding);
            }

            // Interactive Q&A
            logger.info("Ready for questions. Type 'exit' to quit.");
            while (true) {
                System.out.print("\nYour question: ");
                String question = scanner.nextLine();

                if ("exit".equalsIgnoreCase(question)) {
                    break;
                }

                // Retrieve relevant context
                float[] questionEmbedding = EmbeddingGenerator.generateEmbedding(question);
                List<String> relevantChunks = vectorStore.findSimilarDocuments(questionEmbedding, 3);
                String context = String.join("\n\n", relevantChunks);

                // Get answer from DeepSeek
                String answer = deepSeekClient.getAnswer(question, context);
                System.out.println("\nAnswer: " + answer);
            }

            logger.info("Application exiting");
        } catch (Exception e) {
            logger.error("An error occurred: ", e);
        }
    }
}
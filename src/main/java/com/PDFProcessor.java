package com;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class PDFProcessor {
    private static final Logger logger = LoggerFactory.getLogger(PDFProcessor.class);

    public String extractTextFromPDF(String filePath) throws IOException {
        logger.debug("Extracting text from PDF: {}", filePath);

        // Correct loading method for PDFBox 3.x
        try (PDDocument document = Loader.loadPDF(new File(filePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            logger.debug("Extracted {} characters from PDF", text.length());
            return text;
        }
    }
}
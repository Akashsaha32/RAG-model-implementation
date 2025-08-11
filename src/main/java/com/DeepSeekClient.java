package com;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DeepSeekClient {
    private static final Logger logger = LoggerFactory.getLogger(DeepSeekClient.class);
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final Gson gson = new Gson();

    private final OkHttpClient httpClient;
    private final String apiKey;

    public DeepSeekClient(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = new OkHttpClient();
    }

    public String getAnswer(String question, String context) throws IOException {
        logger.debug("Sending question to DeepSeek API with context length: {}", context.length());

        // Construct the prompt with context
        String prompt = String.format(
                "Please answer the following question based on the provided context.\n\n" +
                        "Context:\n%s\n\nQuestion: %s\n\nAnswer:",
                context, question
        );

        // Create the request body
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "deepseek-chat");

        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);

        JsonObject[] messages = { message };
        requestBody.add("messages", gson.toJsonTree(messages));

        RequestBody body = RequestBody.create(
                gson.toJson(requestBody),
                MediaType.parse("application/json")
        );

        // Build the request
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        // Execute the request
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("API request failed with code: {}", response.code());
                throw new IOException("API request failed: " + response.code());
            }

            String responseBody = response.body().string();
            logger.debug("Received API response: {}", responseBody);

            // Parse the response
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
            return jsonResponse.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        }
    }
}
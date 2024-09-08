package com.softgallery.story_playground_server.config;

public class GptConfig {
    // ChatGPT
    public static final String DEFAULT_MODEL = "gpt-4-turbo";
    public static final String AUTHORIZATION = "Authorization";
    public static final String Bearer = "Bearer ";
    public static final String BASE_URL = "https://api.openai.com/v1";
    public static final String GPT_URI = "/chat/completions";

    public static final String FULL_GPT_URI = BASE_URL + GPT_URI;


    // DALL-E
    public static final String DALLE_URI = "/images/generations";
    public static final String DALLE_PROMPT = "그림을 그려줘";
    public static final String DALLE_SIZE = "1024x1024";
}

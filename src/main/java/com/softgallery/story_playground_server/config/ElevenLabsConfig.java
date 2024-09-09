package com.softgallery.story_playground_server.config;

public class ElevenLabsConfig {
    public static final String BASE_URL = "https://api.elevenlabs.io/v1/text-to-speech/";
    public static final String MODEL_ID = "eleven_multilingual_v2";
    public static final String OUTPUT_FORMAT = "mp3_44100_128";
    public static final Float STABILITY = 0.5f;    // 0~1 사이 부동소수점
    public static final Float SIMILARITY_BOOST = 0.5f;    // 0~1 사이 부동소수점
}

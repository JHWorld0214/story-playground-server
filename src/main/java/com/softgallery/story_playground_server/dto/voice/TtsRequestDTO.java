package com.softgallery.story_playground_server.dto.voice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TtsRequestDTO {
    private String text;
    @JsonProperty("model_id")
    private String modelId;
    @JsonProperty("output_format")
    private String outputFormat;
    @JsonProperty("voice_settings")
    private VoiceSettings voiceSettings;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VoiceSettings {
        private Float stability;
        @JsonProperty("similarity_boost")
        private Float similarityBoost;
    }
}

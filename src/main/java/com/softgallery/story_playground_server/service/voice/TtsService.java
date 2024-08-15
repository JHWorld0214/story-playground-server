package com.softgallery.story_playground_server.service.voice;

import com.softgallery.story_playground_server.config.ElevenLabsConfig;
import com.softgallery.story_playground_server.dto.voice.TtsRequestDTO;
import com.softgallery.story_playground_server.dto.voice.TtsRequestDTO.VoiceSettings;
import java.io.File;
import java.nio.file.Files;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
@Transactional
public class TtsService {
    private final WebClient webClient;
    private final VoiceId voiceId = new VoiceId();
    private final String audioPath = "src/main/resources/static/voice";

    @Value("${eleven.api.key}")
    private String apiKey;
    private Long audioFileIndex = 1L;

    public File convertText(String textToConvert) {
        TtsRequestDTO ttsRequestDTO = TtsRequestDTO.builder()
                .text(textToConvert)
                .modelId(ElevenLabsConfig.MODEL_ID)
                .outputFormat(ElevenLabsConfig.OUTPUT_FORMAT)
                .voiceSettings(new VoiceSettings(ElevenLabsConfig.STABILITY, ElevenLabsConfig.SIMILARITY_BOOST))
                .build();

        System.out.println(ttsRequestDTO.getVoiceSettings().getSimilarityBoost() + " " + ttsRequestDTO.getVoiceSettings().getStability());

        File audioFile = null;
        try {
            byte[] response = webClient.post()
                    .uri(ElevenLabsConfig.BASE_URL + voiceId.getVoiceOf("DoHyun"))
                    .header("Accept", "audio/mpeg")
                    .header("Content-Type", "application/json")
                    .header("xi-api-key", apiKey)
                    .bodyValue(ttsRequestDTO)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            if (response != null) {
                audioFile = new File(audioPath + "stt" + audioFileIndex + ".mp3");
                Files.write(audioFile.toPath(), response);
                System.out.println("Audio file saved successfully: " + audioFile.getAbsolutePath());

                audioFileIndex += 1;
            } else {
                System.out.println("Error reating audio file : No response received from the API.");
            }


        } catch (Exception ex) {
            System.out.println("Error creating audio file : " + ex.toString());
        }
        
        return audioFile;
    }
}

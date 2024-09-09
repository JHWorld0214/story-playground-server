package com.softgallery.story_playground_server.service.voice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softgallery.story_playground_server.config.ClovaConfig;
import com.softgallery.story_playground_server.dto.voice.SttResponseDTO;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
@Transactional
public class SttService {
    private final WebClient webClient;

    @Value("${clova.api.id}")
    private String apiId;
    @Value("${clova.api.key}")
    private String apiKey;

    public String convertSpeech(final MultipartFile audioFile) throws IOException {
        String responseText = null;
        byte[] arrayFile = audioFile.getBytes();

        try {
            SttResponseDTO response = webClient.post()
                    .uri(ClovaConfig.BASE_URL + "?lang=" + ClovaConfig.LANG_CODE)
                    .header(ClovaConfig.HEADER_ID, apiId)
                    .header(ClovaConfig.HEADER_KEY, apiKey)
                    .header("Content-Type", ClovaConfig.CONTENT_TYPE)
                    .bodyValue(arrayFile)  // byte[] 데이터를 직접 요청 본문에 포함
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            clientResponse -> clientResponse.bodyToMono(String.class).flatMap(errorBody -> {
                                // 에러 응답을 파싱하여 errorCode와 메시지를 출력
                                try {
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    JsonNode root = objectMapper.readTree(errorBody);
                                    String errorCode = root.path("error").path("errorCode").asText();
                                    String errorMessage = root.path("error").path("message").asText();
                                    System.out.println("Error Code: " + errorCode + ", Message: " + errorMessage);
                                } catch (Exception e) {
                                    System.out.println("Error parsing error response: " + e.getMessage());
                                }
                                return Mono.error(new RuntimeException("API Error: " + errorBody));
                            })
                    )
                    .bodyToMono(SttResponseDTO.class)
                    .block();

            System.out.println(response.toString());
            responseText = response.getText();

            if (responseText != null) {
                System.out.println("converted text data : " + responseText);
            } else {
                System.out.println("Failed reading audio file : No response received from the API.");
            }
        } catch (Exception ex) {
            System.out.println("Error creating text : " + ex.toString());
        }

        return responseText;
    }
}
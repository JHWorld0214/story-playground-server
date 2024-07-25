package com.softgallery.story_playground_server.service.chatgpt;

import com.softgallery.story_playground_server.config.ChatGptConfig;
import com.softgallery.story_playground_server.dto.chatgpt.ChatGptRequestDTO;
import com.softgallery.story_playground_server.dto.chatgpt.ChatGptResponseDTO;
import com.softgallery.story_playground_server.dto.chatgpt.QuestionRequestDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class ChatGptService {

    private static RestTemplate restTemplate = new RestTemplate();

    public HttpEntity<ChatGptRequestDTO> buildHttpEntity(ChatGptRequestDTO requestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(ChatGptConfig.MEDIA_TYPE));
        headers.add(ChatGptConfig.AUTHORIZATION, ChatGptConfig.BEARER + ChatGptConfig.API_KEY);
        return new HttpEntity<>(requestDto, headers);
    }

    public ChatGptResponseDTO getResponse(HttpEntity<ChatGptRequestDTO> chatGptRequestDtoHttpEntity) {
        ResponseEntity<ChatGptResponseDTO> responseEntity = restTemplate.postForEntity(
                ChatGptConfig.URL,
                chatGptRequestDtoHttpEntity,
                ChatGptResponseDTO.class);

        return responseEntity.getBody();
    }

    public ChatGptResponseDTO askQuestion(QuestionRequestDTO requestDto) {
        return this.getResponse(
                this.buildHttpEntity(
                        new ChatGptRequestDTO(
                                ChatGptConfig.MODEL,
                                requestDto.getMessages(),
                                ChatGptConfig.MAX_TOKEN,
                                ChatGptConfig.TEMPERATURE,
                                ChatGptConfig.TOP_P
                        )
                )
        );
    }
}

package com.softgallery.story_playground_server.dto.chatgpt;

import com.softgallery.story_playground_server.service.chatgpt.Message;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;

@Getter
public class QuestionRequestDTO implements Serializable {
    private List<Message> messages;

    public QuestionRequestDTO() { }

    public QuestionRequestDTO(List<Message> messages) {
        this.messages = messages;
    }
}

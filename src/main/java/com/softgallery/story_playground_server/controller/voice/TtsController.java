package com.softgallery.story_playground_server.controller.voice;

import com.softgallery.story_playground_server.dto.voice.TtsRequestDTO;
import com.softgallery.story_playground_server.global.common.SuccessResponse;
import com.softgallery.story_playground_server.service.voice.SttService;
import com.softgallery.story_playground_server.service.voice.TtsService;
import java.io.File;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/voice")
public class TtsController {
    private final TtsService ttsService;
    private final SttService sttService;

    public TtsController(final TtsService ttsService, final SttService sttService) {
        this.ttsService = ttsService;
        this.sttService = sttService;
    }

    @PostMapping("/tts")
    public ResponseEntity<SuccessResponse<?>> textToSpeech(@RequestBody Map<String, String> textToConvert) {
        File audioFile = ttsService.convertText(textToConvert.get("textToConvert"));
        return SuccessResponse.ok(audioFile);
    }

//    @PostMapping("/stt")
//    public ResponseEntity<SuccessResponse<?>> speechToText(@RequestBody String speechToConvert) {
//        SttRequestDTO sttRequestDTO = sttService.convertText(textToConvert);
//        return SuccessResponse.ok(sttRequestDTO);
//    }

}

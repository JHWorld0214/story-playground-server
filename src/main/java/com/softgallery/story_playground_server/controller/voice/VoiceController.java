package com.softgallery.story_playground_server.controller.voice;

import com.softgallery.story_playground_server.global.common.SuccessResponse;
import com.softgallery.story_playground_server.service.voice.SttService;
import com.softgallery.story_playground_server.service.voice.TtsService;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/voice")
public class VoiceController {
    private final TtsService ttsService;
    private final SttService sttService;

    public VoiceController(final TtsService ttsService, final SttService sttService) {
        this.ttsService = ttsService;
        this.sttService = sttService;
    }

    @PostMapping("/tts")
    public ResponseEntity<SuccessResponse<?>> textToSpeech(@RequestBody Map<String, String> textToConvert) {
        File audioFile = ttsService.convertText(textToConvert.get("textToConvert"));
        return SuccessResponse.ok(audioFile);
    }

    @PostMapping(value = "/stt", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> speechToText(@RequestPart("speechToConvert") MultipartFile speechToConvert) throws IOException {
        String textFile = sttService.convertSpeech(speechToConvert);
        return ResponseEntity.ok(textFile);
    }
}

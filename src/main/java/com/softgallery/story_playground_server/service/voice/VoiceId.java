package com.softgallery.story_playground_server.service.voice;

import java.util.HashMap;
import java.util.Map;

public class VoiceId {
    private String defaultName = "DoHyun";
    private Map<String, String> voiceIdList = new HashMap<>() {
        {
            put("DoHyun", "FQ3MuLxZh0jHcZmA5vW1");
            put("KKC", "gJSDQIpSQ56NBGhorBfg");
            put("Hyuk", "WqVy7827vjE2r3jWvbnP");
        }
    };

    public String getVoiceOf(final String name) {
        try {
            return voiceIdList.get(name);
        } catch (Exception e) {
            System.out.println("No voice of such name");
            return voiceIdList.get(defaultName);
        }
    }
}

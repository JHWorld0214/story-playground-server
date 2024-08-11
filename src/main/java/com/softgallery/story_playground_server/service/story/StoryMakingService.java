package com.softgallery.story_playground_server.service.story;

import com.softgallery.story_playground_server.service.auth.JWTUtil;
import com.softgallery.story_playground_server.service.chatgpt.GPTPromptingInfo;
import com.softgallery.story_playground_server.dto.story.*;
import com.softgallery.story_playground_server.dto.chatgpt.*;
import com.softgallery.story_playground_server.dto.moderation.*;
import com.softgallery.story_playground_server.entity.StoryEntity;
import com.softgallery.story_playground_server.repository.StoryEvaluationRepository;
import com.softgallery.story_playground_server.repository.StoryRepository;
import com.softgallery.story_playground_server.service.chatgpt.ChatGptService;
import com.softgallery.story_playground_server.service.chatgpt.Choice;
import com.softgallery.story_playground_server.service.chatgpt.Message;
import com.softgallery.story_playground_server.service.moderation.WordFilter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class StoryMakingService {
    private final ChatGptService chatGptService;
    private final StoryRepository storyRepository;

    private final StoryEvaluationRepository storyEvaluationRepository;
    private final GPTPromptingInfo gptPromptingInfo = new GPTPromptingInfo();
    private final JWTUtil jwtUtil;

    public StoryMakingService (final ChatGptService chatGptService, final StoryRepository storyRepository,
                               StoryEvaluationRepository storyEvaluationRepository, final JWTUtil jwtUtil) {
        this.chatGptService = chatGptService;
        this.storyRepository = storyRepository;
        this.storyEvaluationRepository = storyEvaluationRepository;
        this.jwtUtil = jwtUtil;
    }


    private List<Map<String, String>> parseContents(StoryDTO storyDTO) {
        List<Map<String, String>> parsedContents = new ArrayList<>();

        // 정규 표현식을 사용하여 <태그>를 기준으로 문자열을 분리
        Pattern pattern = Pattern.compile("(<[^>]+>)(.*?)(?=<[^>]+>|$)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(storyDTO.getContent());

        while (matcher.find()) {
            String tag = matcher.group(1);
            String content = matcher.group(2).trim();

            // 태그와 내용을 출력
            System.out.println("Tag: " + tag);
            System.out.println("Content: " + content);

            HashMap<String, String> paragraph = new HashMap<>();
            paragraph.put(tag, content);
            parsedContents.add(paragraph);
        }

        return parsedContents;
    }

    private String createGPTQuery(String prevStory) {
        if(prevStory.isEmpty()) {   // 이전 이야기가 없는 경우 (작성 시작 시)
//            CharacterDTO mainCharacter = characters.get(0);
//            CharacterDTO villain = characters.get(1);

            String result = gptPromptingInfo.getInitalizingMessage() + "\n"
                    + gptPromptingInfo.getStyleOptimizingMessage() + "\n\n"
                    + "너는 위 정보를 가지고 이야기의 도입부분을 한국어로 만들어줘야해.\n"
                    ;

            return result;
        }
        else {  // 이야기에 문장 추가
            String result = gptPromptingInfo.getStoryContinuingMessage(prevStory);
            return result;
        }
    }

    private List<String> getRecommendedTitleAndTopic(String content) {
        String queryStatement = gptPromptingInfo.getTitleAndTopicRecommendationMessage()
                + "\n" + content;

        Message message = new Message("system", queryStatement);
        List<Message> messages = new ArrayList<>();
        messages.add(message);

        ChatGptResponseDTO responseDTO = chatGptService.askQuestion(new QuestionRequestDTO(messages));
        Choice choice = responseDTO.getChoices().get(0);

        String title = extractContent(choice.getMessage().getContent(), "title", "topic");
        String topic = extractContent(choice.getMessage().getContent(), "topic", null);

        List<String> values = new ArrayList<>();
        values.add(choice.getMessage().getContent());

        List<String> titleAndTopic = new ArrayList<String>();
        titleAndTopic.add(title);
        titleAndTopic.add(topic);

        return titleAndTopic;
    }

    public static String extractContent(String input, String startTag, String nextTag) {
        String startTagString = "<" + startTag + ">";
        int startIndex = input.indexOf(startTagString) + startTagString.length();
        int endIndex = (nextTag != null) ? input.indexOf("<" + nextTag + ">", startIndex) : input.length();

        if (startIndex >= startTagString.length() && (endIndex > startIndex || nextTag == null)) {
            return input.substring(startIndex, endIndex).trim();
        }
        return null;
    }

    public StoryDTO createStory(String userToken) {
        String username = jwtUtil.getUsername(JWTUtil.getOnlyToken(userToken));

        // 시스템 프롬프트 적용
        Message message = new Message("system", createGPTQuery(""));
        List<Message> messages = new ArrayList<>();
        messages.add(message);

        // 프롬프트 적용 후 쿼리 날려서 받아오기
        ChatGptResponseDTO responseDTO = chatGptService.askQuestion(new QuestionRequestDTO(messages));
        Choice choice = responseDTO.getChoices().get(0);

        StoryEntity currentStory = new StoryEntity();
        currentStory.setTitle("No Title");
        currentStory.setUsername(username);
        currentStory.setTopic("Default Topic");
        currentStory.setLevel(1L);
        currentStory.setIsCompleted(false);
        currentStory.setContent("<gpt>\n" + choice.getMessage().getContent());
        currentStory.setModifiedDate(LocalDateTime.now());
        currentStory.setLikeNum(0L);
        currentStory.setDislikeNum(0L);
        currentStory.setVisibility(Visibility.PRIVATE);

        StoryEntity savedStory = storyRepository.save(currentStory);
        StoryDTO savedStoryDTO = new StoryDTO(savedStory);
        savedStoryDTO.setContent(choice.getMessage().getContent());

        return savedStoryDTO;
    }

    public StoryDTO addContentToStory(String userToken, Long storyId, Map<String, String> userInput) {
        Optional<StoryEntity> storyEntityOptional = storyRepository.findById(storyId);
        if(storyEntityOptional.isEmpty()) throw new RuntimeException("No such story");

        StoryEntity previousStoryEntity = storyEntityOptional.get();
        StoryDTO previousStoryDTO = new StoryDTO(previousStoryEntity);

        String authorName = jwtUtil.getUsername(JWTUtil.getOnlyToken(userToken));
        if(!authorName.equals(previousStoryDTO.getUsername())) throw new RuntimeException("No Permission to Modify");

        if(previousStoryEntity.getIsCompleted()) throw new RuntimeException("Already completed story");

        // Invalid Story 예외처리 통과 후 1차 Moderation 체크
        WordFilterDTO filterResult = WordFilter.doFilterWithGptModeration(userInput.get("newStory"));
        if(filterResult.isBad()) return new StoryDTO(WordFilter.getBadDataIndicator(), false);

        // 1차 Moderation 통과 후 4o 모델을 사용한 2차 체크 -> 통과 시 다음 문장 생성
        String updatedContent = previousStoryDTO.getContent() + "\n<user>\n" + userInput.get("newStory");

        Message message = new Message("system", createGPTQuery(updatedContent));
        List<Message> messages = new ArrayList<>();
        messages.add(message);

        // 유저가 보낸 내용에 따라 GPT가 응답 생성
        ChatGptResponseDTO responseDTO = chatGptService.askQuestion(new QuestionRequestDTO(messages));
        Choice choice = responseDTO.getChoices().get(0);

        // GPT의 2차 Moderation check를 통과하지 못한 경우
        if(choice.getMessage().getContent().contains(gptPromptingInfo.getModerationDetectingMessage())) {
            return new StoryDTO(WordFilter.getBadDataIndicator(), false);
        }

        String addedSentence = updatedContent + "\n<gpt>\n" + choice.getMessage().getContent();

        // 수정시간 최신화 후 GPT 응답을 포함하여 DB 저장 준비
        previousStoryEntity.setModifiedDate(LocalDateTime.now());
        previousStoryEntity.setContent(addedSentence);

        String ret=choice.getMessage().getContent();

        // 이야기 종료
        if(choice.getMessage().getContent().contains(gptPromptingInfo.getClosingMessage())) {
            previousStoryEntity.setIsCompleted(true);

            List<String> values = getRecommendedTitleAndTopic(addedSentence);
            previousStoryEntity.setTitle(values.get(0));   // Title
            previousStoryEntity.setTopic(values.get(1));   // Topic

            ret = ret.replace("### 이야기 종료 ###", "");

            String lastSentence = updatedContent + "\n<gpt>\n" + ret;
            previousStoryEntity.setContent(lastSentence);
        }

        StoryEntity updatedStoryEntity = storyRepository.save(previousStoryEntity);
        StoryDTO updatedStoryDTO = new StoryDTO(updatedStoryEntity);

        updatedStoryDTO.setContent(ret);   // 가장 마지막에 만든 문장만 반환하도록 처리


        return updatedStoryDTO;
    }

    public boolean changeStoryStateIncomplete(Long storyId) {
        Optional<StoryEntity> storyEntityOptional = storyRepository.findById(storyId);
        if(storyEntityOptional.isEmpty()) throw new RuntimeException("No such story");

        StoryEntity story = storyEntityOptional.get();
        story.setIsCompleted(false);
        return true;
    }

    public List<String> findStoryByStoryId(Long storyId) {
        Optional<StoryEntity> storyEntityOptional = storyRepository.findById(storyId);
        if(storyEntityOptional.isEmpty()) throw new RuntimeException("No such story");

        StoryDTO storyDTO = new StoryDTO(storyEntityOptional.get());
        return StoryDTO.parseContents(storyDTO);
    }

    public boolean changeStoryVisibility(Long storyId, String visibility) {
        Optional<StoryEntity> storyEntityOptional = storyRepository.findById(storyId);
        if(storyEntityOptional.isEmpty()) throw new RuntimeException("No such story");

        StoryEntity storyEntity = storyEntityOptional.get();
        if(visibility.equals("PRIVATE"))
            storyEntity.setVisibility(Visibility.PRIVATE);
        else if(visibility.equals("PUBLIC"))
            storyEntity.setVisibility(Visibility.PUBLIC);
        else
            throw new RuntimeException("Invalid Visibility Value");

        storyRepository.save(storyEntity);
        return true;
    }

    public StoryReadingDTO getStory(Long storyId, String token) {
        Optional<StoryEntity> story = storyRepository.findById(storyId);
        if(!story.isPresent()) throw new RuntimeException("story entity 없음");
        else {
            String username = jwtUtil.getUsername(JWTUtil.getOnlyToken(token));
            StoryEntity currStory = story.get();

            Boolean isEvaluated = storyEvaluationRepository.existsByUsernameAndStoryId(username, currStory.getStoryId());

            StoryReadingDTO storyReadingDTO = new StoryReadingDTO(
                    currStory.getStoryId(), currStory.getTitle(), currStory.getUsername(),
                    currStory.getTopic(), currStory.getIsCompleted(), currStory.getModifiedDate(), currStory.getVisibility(),
                    currStory.getLikeNum(), currStory.getDislikeNum(), username.equals(currStory.getUsername()), isEvaluated
                    );
            return storyReadingDTO;
        }
    }
}

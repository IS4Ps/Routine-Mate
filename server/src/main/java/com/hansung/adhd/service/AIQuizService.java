package com.hansung.adhd.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansung.adhd.domain.AIGeneratedQuizzes;
import com.hansung.adhd.domain.Children;
import com.hansung.adhd.dto.AIQuizDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.AIQuizRepository;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIQuizService {

    private final AIQuizRepository aiQuizRepository;
    private final ChildrenRepository childrenRepository;
    private final ObjectMapper objectMapper;

    @Value("${OPENAI_API_KEY}")
    private String openaiApiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private static final int REWARD_GOLD = 10;

    // 이미지로 OX 퀴즈 생성 (여러 장 지원)
    @Transactional
    public AIQuizDto.GenerateResponse generateQuizzes(AIQuizDto.GenerateRequest request) {
        Children child = childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        List<AIGeneratedQuizzes> allQuizzes = new ArrayList<>();

        // 여러 장 이미지 처리
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (AIQuizDto.ImageRequest imageRequest : request.getImages()) {
                List<Map<String, String>> quizData = callOpenAI(
                        imageRequest.getImageBase64(),
                        request.getCategory()
                );
                allQuizzes.addAll(toQuizEntities(child, request.getCategory(), quizData));
            }
        }
        // 단일 이미지 하위 호환
        else if (request.getImageBase64() != null) {
            List<Map<String, String>> quizData = callOpenAI(
                    request.getImageBase64(),
                    request.getCategory()
            );
            allQuizzes.addAll(toQuizEntities(child, request.getCategory(), quizData));
        }

        aiQuizRepository.saveAll(allQuizzes);

        List<AIQuizDto.QuizResponse> responses = allQuizzes.stream()
                .map(AIQuizDto.QuizResponse::from)
                .collect(Collectors.toList());

        return AIQuizDto.GenerateResponse.builder()
                .totalCount(responses.size())
                .quizzes(responses)
                .build();
    }

    // 퀴즈 목록 조회
    @Transactional(readOnly = true)
    public List<AIQuizDto.QuizResponse> getQuizzes(Long childId, Boolean unsolved) {
        List<AIGeneratedQuizzes> quizzes;
        if (Boolean.TRUE.equals(unsolved)) {
            quizzes = aiQuizRepository.findByChildIdAndIsSolvedFalse(childId);
        } else {
            quizzes = aiQuizRepository.findByChildIdOrderByCreatedAtDesc(childId);
        }
        return quizzes.stream().map(AIQuizDto.QuizResponse::from).collect(Collectors.toList());
    }

    // 정답 제출
    @Transactional
    public AIQuizDto.AnswerResponse submitAnswer(Long quizId, AIQuizDto.AnswerRequest request) {
        AIGeneratedQuizzes quiz = aiQuizRepository.findById(quizId)
                .orElseThrow(() -> new CustomException(ErrorCode.QUIZ_NOT_FOUND));

        quiz.submitAnswer(request.getAnswer());

        int rewardGold = 0;
        if (quiz.getIsCorrect()) {
            quiz.getChild().addGold(REWARD_GOLD);
            rewardGold = REWARD_GOLD;
        }

        return AIQuizDto.AnswerResponse.builder()
                .quizId(quiz.getId())
                .isCorrect(quiz.getIsCorrect())
                .correctAnswer(quiz.getCorrectAnswer())
                .explanation(quiz.getExplanation())
                .rewardGold(rewardGold)
                .build();
    }

    // 엔티티 변환 헬퍼
    private List<AIGeneratedQuizzes> toQuizEntities(Children child, String category,
                                                    List<Map<String, String>> quizData) {
        return quizData.stream()
                .map(data -> AIGeneratedQuizzes.create(
                        child,
                        category,
                        data.get("question"),
                        data.get("answer"),
                        data.get("explanation")
                ))
                .collect(Collectors.toList());
    }

    // OpenAI API 호출 (문제 수는 AI가 알아서 3~5개 생성)
    private List<Map<String, String>> callOpenAI(String imageBase64, String category) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        String prompt = String.format(
                "다음 이미지는 학습 자료입니다. 이미지의 핵심 내용을 바탕으로 " +
                        "내용의 양과 중요도에 따라 3~5개의 OX 퀴즈를 만들어주세요.\n" +
                        "과목: %s\n\n" +
                        "반드시 아래 JSON 배열 형식으로만 응답하세요. 다른 텍스트는 포함하지 마세요:\n" +
                        "[\n" +
                        "  {\n" +
                        "    \"question\": \"퀴즈 문제\",\n" +
                        "    \"answer\": \"O 또는 X\",\n" +
                        "    \"explanation\": \"정답 해설\"\n" +
                        "  }\n" +
                        "]",
                category
        );

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "max_tokens", 1000,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", List.of(
                                        Map.of("type", "text", "text", prompt),
                                        Map.of("type", "image_url",
                                                "image_url", Map.of(
                                                        "url", "data:image/jpeg;base64," + imageBase64,
                                                        "detail", "low"
                                                ))
                                )
                        )
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_URL, entity, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = (String) message.get("content");
            return objectMapper.readValue(content, new TypeReference<List<Map<String, String>>>() {});
        } catch (Exception e) {
            log.error("OpenAI API 호출 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.AI_QUIZ_GENERATION_FAILED);
        }
    }
}
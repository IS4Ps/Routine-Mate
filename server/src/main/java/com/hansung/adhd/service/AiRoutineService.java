package com.hansung.adhd.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansung.adhd.domain.Children;
import com.hansung.adhd.dto.AiRoutineDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiRoutineService {

    private final ChildrenRepository childrenRepository;
    private final ObjectMapper objectMapper;

    @Value("${OPENAI_API_KEY}")
    private String openaiApiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    public AiRoutineDto.Response recommendRoutines(AiRoutineDto.Request request) {
        Children child = childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        // 1. 프롬프트 작성 (아이의 스탯을 기반으로 맞춤형 추천!)
        String prompt = String.format(
                "당신은 ADHD 아동의 행동 발달을 돕는 전문가 코치입니다.\n" +
                        "이 아이의 현재 레벨은 %d이고, 스탯은 체력 %d, 지능 %d, 창의력 %d 입니다.\n" +
                        "이 아이의 부족한 스탯을 보완하거나 성취감을 줄 수 있는 일상 미션(루틴) 3가지를 추천해주세요.\n\n" +
                        "반드시 아래 JSON 배열 형식으로만 응답하세요. 다른 텍스트는 절대 포함하지 마세요:\n" +
                        "[\n" +
                        "  {\n" +
                        "    \"title\": \"미션 제목 (예: 5분 동안 책상 정리하기)\",\n" +
                        "    \"description\": \"미션 상세 설명 및 칭찬 멘트\",\n" +
                        "    \"exp\": 10\n" +
                        "  }\n" +
                        "]",
                child.getLevel(), child.getStatStrength(), child.getStatIntelligence(), child.getStatCreativity()
        );

        // 2. OpenAI API 호출
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini", // 팀장님이 쓰신 가성비 모델 그대로!
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_URL, entity, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            String content = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");

            log.info("AI 루틴 추천 응답: {}", content);

            // 3. JSON 파싱해서 DTO 리스트로 변환
            List<AiRoutineDto.Recommendation> recommendations =
                    objectMapper.readValue(content, new TypeReference<List<AiRoutineDto.Recommendation>>() {});

            return new AiRoutineDto.Response(recommendations);

        } catch (Exception e) {
            log.error("AI 루틴 추천 실패: {}", e.getMessage());
            throw new CustomException(ErrorCode.AI_QUIZ_GENERATION_FAILED); // 일단 팀장님이 만든 에러코드 재활용
        }
    }
}
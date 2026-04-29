package com.hansung.adhd.service;

import com.hansung.adhd.domain.Children;
import com.hansung.adhd.domain.MinigameLogs;
import com.hansung.adhd.dto.MinigameDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.repository.MinigameLogsRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NBackService {

    private final ChildrenRepository childrenRepository;
    private final MinigameLogsRepository minigameLogsRepository;
    private final MinigameSessionStore sessionStore;

    // 자극 이미지 목록 (Flutter에서 이 키로 이미지 매핑)
    private static final List<String> ITEMS = List.of(
            "apple", "banana", "cherry", "grape",
            "orange", "strawberry", "watermelon", "peach"
    );

    // N값에 따른 자극 표시 시간 (ms)
    private int getDisplayTime(int nLevel) {
        return switch (nLevel) {
            case 3 -> 1500;
            case 4 -> 1200;
            default -> 2000; // 2-Back 기본
        };
    }

    // N값에 따른 자극 간격 (ms)
    private int getIntervalTime(int nLevel) {
        return switch (nLevel) {
            case 3 -> 800;
            case 4 -> 600;
            default -> 1000;
        };
    }

    // ── 게임 시작 ─────────────────────────────────────────────────────────────
    public MinigameDto.NBackStartResponse startGame(MinigameDto.NBackStartRequest request) {
        childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        int total  = request.getTotalCount();
        int nLevel = request.getNLevel();

        Random random = new Random();
        List<MinigameDto.NBackStimulus> stimuli = new ArrayList<>();

        // 처음 N개는 비교 대상이 없으므로 랜덤 생성
        for (int i = 0; i < total; i++) {
            String value;
            // N번 전과 일치하는 자극을 약 30% 확률로 생성
            if (i >= nLevel && random.nextDouble() < 0.3) {
                value = stimuli.get(i - nLevel).getValue();
            } else {
                // 이전 자극과 다른 값 생성
                String prev = i > 0 ? stimuli.get(i - 1).getValue() : null;
                do {
                    value = ITEMS.get(random.nextInt(ITEMS.size()));
                } while (value.equals(prev));
            }

            stimuli.add(MinigameDto.NBackStimulus.builder()
                    .index(i + 1)
                    .value(value)
                    .build());
        }

        String sessionId = UUID.randomUUID().toString();
        sessionStore.saveNBack(sessionId, stimuli, nLevel);

        return MinigameDto.NBackStartResponse.builder()
                .sessionId(sessionId)
                .nLevel(nLevel)
                .stimuli(stimuli)
                .displayTime(getDisplayTime(nLevel))
                .intervalTime(getIntervalTime(nLevel))
                .totalCount(total)
                .build();
    }

    // ── 정답 제출 및 채점 ─────────────────────────────────────────────────────
    @Transactional
    public MinigameDto.NBackSubmitResponse submitGame(MinigameDto.NBackSubmitRequest request) {
        Children child = childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        MinigameSessionStore.NBackSession session = sessionStore.getNBack(request.getSessionId());
        if (session == null) {
            throw new CustomException(ErrorCode.INVALID_MISSION_STATUS);
        }

        List<MinigameDto.NBackStimulus> stimuli = session.getStimuli();
        int nLevel = session.getNLevel();

        int correct = 0, wrong = 0;
        int judgableCount = 0; // N번째 이후부터 판단 가능

        for (MinigameDto.NBackAnswer answer : request.getAnswers()) {
            int idx = answer.getIndex() - 1; // 0-based index

            // N번 전 자극과 비교 가능한 경우만 채점
            if (idx < nLevel) continue;
            judgableCount++;

            boolean actualMatch = stimuli.get(idx).getValue()
                    .equals(stimuli.get(idx - nLevel).getValue());
            boolean answeredMatch = Boolean.TRUE.equals(answer.getMatched());

            if (actualMatch == answeredMatch) correct++;
            else wrong++;
        }

        double accuracy = judgableCount > 0
                ? Math.round(((double) correct / judgableCount) * 1000) / 10.0 : 0.0;
        int score      = (int) (accuracy * 10);
        int rewardGold = accuracy >= 90 ? 30 : accuracy >= 70 ? 20 : accuracy >= 50 ? 10 : 0;
        int statGain   = accuracy >= 70 ? 1 : 0;

        if (rewardGold > 0) child.addGold(rewardGold);
        if (statGain > 0)   child.gainStatIntelligence(statGain);

        minigameLogsRepository.save(MinigameLogs.create(child, "N_BACK", score, rewardGold));
        sessionStore.removeNBack(request.getSessionId());

        return MinigameDto.NBackSubmitResponse.builder()
                .correctCount(correct)
                .wrongCount(wrong)
                .totalCount(judgableCount)
                .accuracy(accuracy)
                .score(score)
                .rewardGold(rewardGold)
                .statIntelligenceGain(statGain)
                .nLevel(nLevel)
                .build();
    }
}

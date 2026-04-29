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
public class StroopService {

    private final ChildrenRepository childrenRepository;
    private final MinigameLogsRepository minigameLogsRepository;
    private final MinigameSessionStore sessionStore;

    // 색상 목록
    private static final List<String> COLORS = List.of("빨강", "파랑", "초록", "노랑");

    // 난이도별 불일치 비율 (글자와 색이 다른 문제 비율)
    private double getMismatchRatio(int difficulty) {
        return switch (difficulty) {
            case 2 -> 0.6;
            case 3 -> 0.8;
            default -> 0.4;
        };
    }

    // 난이도별 제한 시간 (ms)
    private int getTimeLimit(int difficulty) {
        return switch (difficulty) {
            case 2 -> 2000;
            case 3 -> 1500;
            default -> 3000;
        };
    }

    // ── 게임 시작 ─────────────────────────────────────────────────────────────
    public MinigameDto.StroopStartResponse startGame(MinigameDto.StroopStartRequest request) {
        childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        int total         = request.getTotalCount();
        int difficulty    = request.getDifficulty();
        int mismatchCount = (int) Math.round(total * getMismatchRatio(difficulty));
        int matchCount    = total - mismatchCount;

        Random random = new Random();
        List<MinigameDto.StroopStimulus> stimuli = new ArrayList<>();

        // 일치 문제 (글자 색 = 글자 의미)
        for (int i = 0; i < matchCount; i++) {
            String color = COLORS.get(random.nextInt(COLORS.size()));
            stimuli.add(MinigameDto.StroopStimulus.builder()
                    .word(color)
                    .inkColor(color)
                    .isMatch(true)
                    .build());
        }

        // 불일치 문제 (글자 색 ≠ 글자 의미)
        for (int i = 0; i < mismatchCount; i++) {
            String word = COLORS.get(random.nextInt(COLORS.size()));
            String inkColor;
            do {
                inkColor = COLORS.get(random.nextInt(COLORS.size()));
            } while (inkColor.equals(word)); // 글자랑 다른 색이 나올 때까지 반복
            stimuli.add(MinigameDto.StroopStimulus.builder()
                    .word(word)
                    .inkColor(inkColor)
                    .isMatch(false)
                    .build());
        }

        Collections.shuffle(stimuli);

        // index 부여
        for (int i = 0; i < stimuli.size(); i++) {
            stimuli.set(i, MinigameDto.StroopStimulus.builder()
                    .index(i + 1)
                    .word(stimuli.get(i).getWord())
                    .inkColor(stimuli.get(i).getInkColor())
                    .isMatch(stimuli.get(i).getIsMatch())
                    .build());
        }

        String sessionId = UUID.randomUUID().toString();
        sessionStore.saveStroop(sessionId, stimuli);

        return MinigameDto.StroopStartResponse.builder()
                .sessionId(sessionId)
                .stimuli(stimuli)
                .timeLimit(getTimeLimit(difficulty))
                .totalCount(total)
                .build();
    }

    // ── 정답 제출 및 채점 ─────────────────────────────────────────────────────
    @Transactional
    public MinigameDto.StroopSubmitResponse submitGame(MinigameDto.StroopSubmitRequest request) {
        Children child = childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        List<MinigameDto.StroopStimulus> stimuli = sessionStore.getStroop(request.getSessionId());
        if (stimuli == null) {
            throw new CustomException(ErrorCode.INVALID_MISSION_STATUS);
        }

        int correct = 0, wrong = 0;
        long totalResponseTime = 0;
        int responseCount = 0;

        for (MinigameDto.StroopAnswer answer : request.getAnswers()) {
            MinigameDto.StroopStimulus stimulus = stimuli.stream()
                    .filter(s -> s.getIndex().equals(answer.getIndex()))
                    .findFirst().orElse(null);

            if (stimulus == null) continue;

            // 정답: 잉크 색상을 선택해야 함
            if (stimulus.getInkColor().equals(answer.getSelectedColor())) {
                correct++;
            } else {
                wrong++;
            }

            if (answer.getResponseTime() != null && answer.getResponseTime() > 0) {
                totalResponseTime += answer.getResponseTime();
                responseCount++;
            }
        }

        int total          = stimuli.size();
        double accuracy    = Math.round(((double) correct / total) * 1000) / 10.0;
        double avgResponse = responseCount > 0 ? Math.round((double) totalResponseTime / responseCount) : 0;
        int score          = (int) (accuracy * 10);
        int rewardGold     = accuracy >= 90 ? 30 : accuracy >= 70 ? 20 : accuracy >= 50 ? 10 : 0;
        int statGain       = accuracy >= 70 ? 1 : 0;

        if (rewardGold > 0) child.addGold(rewardGold);
        if (statGain > 0)   child.gainStatCreativity(statGain);

        minigameLogsRepository.save(MinigameLogs.create(child, "STROOP", score, rewardGold));
        sessionStore.removeStroop(request.getSessionId());

        return MinigameDto.StroopSubmitResponse.builder()
                .correctCount(correct)
                .wrongCount(wrong)
                .totalCount(total)
                .accuracy(accuracy)
                .avgResponseTime(avgResponse)
                .score(score)
                .rewardGold(rewardGold)
                .statCreativityGain(statGain)
                .build();
    }
}

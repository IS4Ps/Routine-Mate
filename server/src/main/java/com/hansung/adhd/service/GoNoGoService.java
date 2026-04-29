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
public class GoNoGoService {

    private final ChildrenRepository childrenRepository;
    private final MinigameLogsRepository minigameLogsRepository;
    private final MinigameSessionStore sessionStore;

    private static final List<String> GO_IMAGES   = List.of("monster_green", "star_blue", "coin_yellow");
    private static final List<String> NOGO_IMAGES = List.of("bomb_red", "skull_black", "fire_orange");

    // 난이도별 NOGO 비율
    private double getNogoRatio(int difficulty) {
        return switch (difficulty) {
            case 2 -> 0.35;
            case 3 -> 0.45;
            default -> 0.25;
        };
    }

    // 난이도별 자극 제한 시간 (ms)
    private int getTimeLimit(int difficulty) {
        return switch (difficulty) {
            case 2 -> 700;
            case 3 -> 500;
            default -> 1000;
        };
    }

    // ── 게임 시작 ─────────────────────────────────────────────────────────────
    public MinigameDto.GoNoGoStartResponse startGame(MinigameDto.GoNoGoStartRequest request) {
        childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        int total      = request.getTotalCount();
        int difficulty = request.getDifficulty();
        int nogoCount  = (int) Math.round(total * getNogoRatio(difficulty));
        int goCount    = total - nogoCount;

        List<MinigameDto.Stimulus> stimuli = new ArrayList<>();
        for (int i = 0; i < goCount; i++) {
            stimuli.add(MinigameDto.Stimulus.builder()
                    .type("GO")
                    .image(GO_IMAGES.get(i % GO_IMAGES.size()))
                    .build());
        }
        for (int i = 0; i < nogoCount; i++) {
            stimuli.add(MinigameDto.Stimulus.builder()
                    .type("NOGO")
                    .image(NOGO_IMAGES.get(i % NOGO_IMAGES.size()))
                    .build());
        }

        Collections.shuffle(stimuli);

        for (int i = 0; i < stimuli.size(); i++) {
            stimuli.set(i, MinigameDto.Stimulus.builder()
                    .index(i + 1)
                    .type(stimuli.get(i).getType())
                    .image(stimuli.get(i).getImage())
                    .build());
        }

        String sessionId = UUID.randomUUID().toString();
        sessionStore.save(sessionId, stimuli);

        return MinigameDto.GoNoGoStartResponse.builder()
                .sessionId(sessionId)
                .stimuli(stimuli)
                .timeLimit(getTimeLimit(difficulty))
                .totalCount(total)
                .build();
    }

    // ── 정답 제출 및 채점 ─────────────────────────────────────────────────────
    @Transactional
    public MinigameDto.GoNoGoSubmitResponse submitGame(MinigameDto.GoNoGoSubmitRequest request) {
        Children child = childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        List<MinigameDto.Stimulus> stimuli = sessionStore.get(request.getSessionId());
        if (stimuli == null) {
            throw new CustomException(ErrorCode.INVALID_MISSION_STATUS);
        }

        int correct = 0, wrong = 0;
        long totalResponseTime = 0;
        int responseCount = 0;

        for (MinigameDto.Answer answer : request.getAnswers()) {
            MinigameDto.Stimulus stimulus = stimuli.stream()
                    .filter(s -> s.getIndex().equals(answer.getIndex()))
                    .findFirst().orElse(null);

            if (stimulus == null) continue;

            boolean isGo   = "GO".equals(stimulus.getType());
            boolean tapped = Boolean.TRUE.equals(answer.getTapped());

            if ((isGo && tapped) || (!isGo && !tapped)) correct++;
            else wrong++;

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
        if (statGain > 0)   child.gainStatStrength(statGain);

        minigameLogsRepository.save(MinigameLogs.create(child, "GO_NO_GO", score, rewardGold));
        sessionStore.remove(request.getSessionId());

        return MinigameDto.GoNoGoSubmitResponse.builder()
                .correctCount(correct)
                .wrongCount(wrong)
                .totalCount(total)
                .accuracy(accuracy)
                .avgResponseTime(avgResponse)
                .score(score)
                .rewardGold(rewardGold)
                .statStrengthGain(statGain)
                .build();
    }
}

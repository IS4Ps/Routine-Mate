package com.hansung.adhd.service;

import com.hansung.adhd.domain.Children;
import com.hansung.adhd.domain.MinigameLogs;
import com.hansung.adhd.dto.MinigameDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.repository.MinigameLogsRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MinigameService {

    private final MinigameLogsRepository minigameLogsRepository;
    private final ChildrenRepository childrenRepository;

    // 미니게임 기록 목록 조회
    @Transactional(readOnly = true)
    public List<MinigameDto.MinigameResponse> getMinigameLogs(Long childId, String gameType) {
        if (gameType != null) {
            return minigameLogsRepository.findByChildIdAndGameType(childId, gameType)
                    .stream().map(MinigameDto.MinigameResponse::from).toList();
        }
        return minigameLogsRepository.findByChildIdOrderByCreatedAtDesc(childId)
                .stream().map(MinigameDto.MinigameResponse::from).toList();
    }

    // 미니게임 결과 저장
    @Transactional
    public MinigameDto.MinigameResponse saveMinigameLog(MinigameDto.CreateRequest request) {
        Children child = childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        MinigameLogs log = MinigameLogs.create(
                child, request.getGameType(),
                request.getScore(), request.getRewardAmount()
        );

        // 골드 지급
        if (request.getRewardAmount() != null && request.getRewardAmount() > 0) {
            child.addGold(request.getRewardAmount());
        }

        return MinigameDto.MinigameResponse.from(minigameLogsRepository.save(log));
    }
}

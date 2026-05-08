package com.hansung.adhd.service;

import com.hansung.adhd.domain.Children;
import com.hansung.adhd.domain.DailyMissions;
import com.hansung.adhd.domain.OfflineRewards;
import com.hansung.adhd.dto.MissionDto;
import com.hansung.adhd.dto.OfflineRewardDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.repository.DailyMissionsRepository;
import com.hansung.adhd.repository.OfflineRewardsRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfflineRewardService {

    private final OfflineRewardsRepository offlineRewardsRepository;
    private final ChildrenRepository childrenRepository;
    private final DailyMissionsRepository dailyMissionsRepository;

    // 오프라인 보상 목록 조회 (달성 여부 포함)
    @Transactional(readOnly = true)
    public List<OfflineRewardDto.RewardResponse> getRewards(Long childId) {
        return offlineRewardsRepository.findByChildId(childId)
                .stream()
                .map(OfflineRewardDto.RewardResponse::from)
                .toList();
    }

    // 오프라인 보상 등록
    @Transactional
    public OfflineRewardDto.RewardResponse createReward(OfflineRewardDto.CreateRequest request) {
        Children child = childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        OfflineRewards reward = OfflineRewards.create(
                child,
                request.getPeriodType(),
                request.getTargetDays(),
                request.getTargetPercent(),
                request.getRewardPromiseText()
        );

        return OfflineRewardDto.RewardResponse.from(offlineRewardsRepository.save(reward));
    }

    // 보상 달성 여부 확인
    // → 주간 성공률 API와 연동해서 Flutter가 달성 여부 표시
    @Transactional(readOnly = true)
    public Boolean checkRewardEligibility(Long rewardId) {
        OfflineRewards reward = offlineRewardsRepository.findById(rewardId)
                .orElseThrow(() -> new CustomException(ErrorCode.REWARD_NOT_FOUND));

        if ("COMPLETED".equals(reward.getStatus())) return false;

        // 이번 주 월~일 미션 조회
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate sunday = LocalDate.now().with(DayOfWeek.SUNDAY);

        List<DailyMissions> weeklyMissions = dailyMissionsRepository
                .findByChildIdAndDateBetweenAndIsDeletedFalse(reward.getChild().getId(), monday, sunday);

        // 날짜별 그룹핑 후 성공 일수 계산
        Map<LocalDate, List<DailyMissions>> missionsByDate = weeklyMissions.stream()
                .collect(Collectors.groupingBy(DailyMissions::getDate));

        int successDays = 0;
        for (Map.Entry<LocalDate, List<DailyMissions>> entry : missionsByDate.entrySet()) {
            List<DailyMissions> dayMissions = entry.getValue();
            int total = dayMissions.size();
            if (total == 0) continue;

            long completed = dayMissions.stream()
                    .filter(m -> "COMPLETED".equals(m.getStatus()) || "APPROVED".equals(m.getStatus()))
                    .count();

            double rate = (double) completed / total * 100;
            if (rate >= reward.getTargetPercent()) successDays++;
        }

        return successDays >= reward.getTargetDays();
    }

    // 보상 완료 처리
    @Transactional
    public OfflineRewardDto.RewardResponse completeReward(Long rewardId) {
        OfflineRewards reward = offlineRewardsRepository.findById(rewardId)
                .orElseThrow(() -> new CustomException(ErrorCode.REWARD_NOT_FOUND));
        reward.complete();
        return OfflineRewardDto.RewardResponse.from(reward);
    }
}
package com.hansung.adhd.service;

import com.hansung.adhd.domain.Children;
import com.hansung.adhd.domain.OfflineRewards;
import com.hansung.adhd.dto.OfflineRewardDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.repository.OfflineRewardsRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OfflineRewardService {

    private final OfflineRewardsRepository offlineRewardsRepository;
    private final ChildrenRepository childrenRepository;

    // 오프라인 보상 목록 조회
    @Transactional(readOnly = true)
    public List<OfflineRewardDto.RewardResponse> getRewards(Long childId) {
        return offlineRewardsRepository.findByChildId(childId)
                .stream()
                .map(OfflineRewardDto.RewardResponse::from)
                .toList();
    }

    // 오프라인 보상 등록 (부모가 설정)
    @Transactional
    public OfflineRewardDto.RewardResponse createReward(OfflineRewardDto.CreateRequest request) {
        Children child = childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        OfflineRewards reward = OfflineRewards.create(
                child,
                request.getPeriodType(),
                request.getTargetPercent(),
                request.getRewardPromiseText()
        );

        return OfflineRewardDto.RewardResponse.from(offlineRewardsRepository.save(reward));
    }

    // 보상 완료 처리 (부모가 보상 지급 후 완료 처리)
    @Transactional
    public OfflineRewardDto.RewardResponse completeReward(Long rewardId) {
        OfflineRewards reward = offlineRewardsRepository.findById(rewardId)
                .orElseThrow(() -> new CustomException(ErrorCode.REWARD_NOT_FOUND));
        reward.complete();
        return OfflineRewardDto.RewardResponse.from(reward);
    }
}

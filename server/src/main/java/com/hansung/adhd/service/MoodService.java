package com.hansung.adhd.service;

import com.hansung.adhd.domain.Children;
import com.hansung.adhd.domain.MoodLogs;
import com.hansung.adhd.dto.MoodDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.repository.MoodLogsRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MoodService {

    private final MoodLogsRepository moodLogsRepository;
    private final ChildrenRepository childrenRepository;

    // 기분 기록 목록 조회
    @Transactional(readOnly = true)
    public List<MoodDto.MoodResponse> getMoodLogs(Long childId) {
        return moodLogsRepository.findByChildIdOrderByDateDesc(childId)
                .stream()
                .map(MoodDto.MoodResponse::from)
                .toList();
    }

    // 기분 기록 저장
    @Transactional
    public MoodDto.MoodResponse createMoodLog(MoodDto.CreateRequest request) {
        Children child = childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        // 같은 날짜에 이미 기록이 있으면 덮어씀
        MoodLogs moodLog = moodLogsRepository
                .findByChildIdAndDate(request.getChildId(), request.getDate())
                .orElse(null);

        if (moodLog == null) {
            moodLog = MoodLogs.create(child, request.getDate(),
                    request.getPrimaryEmotion(), request.getSecondaryEmotion(), request.getScore());
        } else {
            moodLog.record(request.getPrimaryEmotion(),
                    request.getSecondaryEmotion(), request.getScore());
        }

        return MoodDto.MoodResponse.from(moodLogsRepository.save(moodLog));
    }
}

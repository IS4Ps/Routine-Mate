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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MoodService {

    private final MoodLogsRepository moodLogsRepository;
    private final ChildrenRepository childrenRepository;

    // 감정 기록 목록 조회
    @Transactional(readOnly = true)
    public List<MoodDto.MoodResponse> getMoodLogs(Long childId) {
        return moodLogsRepository.findByChildIdOrderByDateDesc(childId)
                .stream()
                .map(MoodDto.MoodResponse::from)
                .toList();
    }

    // 오늘 감정 조회
    @Transactional(readOnly = true)
    public MoodDto.MoodResponse getTodayMoodLog(Long childId) {
        return moodLogsRepository.findByChildIdAndDate(childId, LocalDate.now())
                .map(MoodDto.MoodResponse::from)
                .orElse(null); // 없으면 null
    }

    // 월간 감정 캘린더 조회
    @Transactional(readOnly = true)
    public MoodDto.MonthlyMoodResponse getMonthlyMoodLogs(Long childId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        List<MoodLogs> logs = moodLogsRepository.findByChildIdAndDateBetweenOrderByDate(
                childId, startDate, endDate);

        // 날짜별로 Map 변환 (Flutter 캘린더 렌더링용)
        Map<String, MoodDto.MoodResponse> moodMap = logs.stream()
                .collect(Collectors.toMap(
                        log -> log.getDate().toString(),
                        MoodDto.MoodResponse::from
                ));

        // 평균 감정 점수 계산
        double avgScore = logs.stream()
                .filter(log -> log.getScore() != null)
                .mapToInt(MoodLogs::getScore)
                .average()
                .orElse(0.0);

        return MoodDto.MonthlyMoodResponse.builder()
                .year(year)
                .month(month)
                .totalCount(logs.size())
                .avgScore(Math.round(avgScore * 10) / 10.0)
                .moodMap(moodMap)
                .build();
    }

    // 감정 기록 저장
    @Transactional
    public MoodDto.MoodResponse createMoodLog(MoodDto.CreateRequest request) {
        Children child = childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

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
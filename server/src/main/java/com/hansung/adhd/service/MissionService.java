package com.hansung.adhd.service;

import com.hansung.adhd.domain.*;
import com.hansung.adhd.dto.MissionDto;
import com.hansung.adhd.exception.CustomException;
import com.hansung.adhd.repository.ChildrenRepository;
import com.hansung.adhd.repository.DailyMissionsRepository;
import com.hansung.adhd.repository.PresetBigTasksRepository;
import com.hansung.adhd.repository.RoutinePresetsRepository;
import com.hansung.adhd.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MissionService {

    private final DailyMissionsRepository dailyMissionsRepository;
    private final ChildrenRepository childrenRepository;
    private final PresetBigTasksRepository presetBigTasksRepository;
    private final RoutinePresetsRepository routinePresetsRepository;

    // 오늘의 미션 목록 조회
    @Transactional(readOnly = true)
    public List<MissionDto.MissionResponse> getTodayMissions(Long childId) {
        return dailyMissionsRepository.findByChildIdAndDate(childId, LocalDate.now())
                .stream()
                .map(MissionDto.MissionResponse::from)
                .collect(Collectors.toList());
    }

    // 미션 생성 (BigTask 선택 → SmallTask 자동 복사)
    @Transactional
    public MissionDto.MissionResponse createMission(MissionDto.CreateRequest request) {
        Children child = childrenRepository.findById(request.getChildId())
                .orElseThrow(() -> new CustomException(ErrorCode.CHILD_NOT_FOUND));

        PresetBigTasks bigTask = presetBigTasksRepository.findById(request.getOriginBigTaskId())
                .orElseThrow(() -> new CustomException(ErrorCode.BIG_TASK_NOT_FOUND));

        DailyMissions mission = DailyMissions.create(
                child,
                bigTask.getPreset().getId(),
                bigTask.getId(),
                bigTask.getPreset().getTitle(),
                bigTask.getTitle(),
                null,
                request.getAssignedExp(),
                request.getDate(),
                request.getStartTime(),
                request.getEndTime()
        );

        return MissionDto.MissionResponse.from(dailyMissionsRepository.save(mission));
    }

    // 미션 시작 (퀘스트 시작 버튼)
    @Transactional
    public MissionDto.MissionResponse startMission(Long missionId) {
        DailyMissions mission = getMissionOrThrow(missionId);
        mission.start(LocalDateTime.now());
        return MissionDto.MissionResponse.from(mission);
    }

    // 미션 완료 처리
    @Transactional
    public MissionDto.MissionResponse completeMission(Long missionId) {
        DailyMissions mission = getMissionOrThrow(missionId);

        // 🚨 중복 완료 방지
        if ("COMPLETED".equals(mission.getStatus())) {
            throw new CustomException(ErrorCode.INVALID_MISSION_STATUS); // "올바르지 않은 미션 상태입니다."
        }

        // 1. 미션 상태를 '완료'로 변경하고 시간 기록
        mission.complete(LocalDateTime.now());

        // 2. 미션을 수행한 아이(Children)를 불러와서 보상
        Children child = mission.getChild();

        // 아이 엔티티에 만들어둔 도메인 메서드 호출
        child.gainExp(mission.getAssignedExp()); // 미션에 걸려있던 경험치 그대로 획득!
        child.addGold(30); // 기본 골드 30 지급 (추후 상의 후 바꾸기)

        // JPA의 '더티 체킹' 덕분에 child.save()를 안 해도 경험치와 골드가 DB에 자동 저장
        return MissionDto.MissionResponse.from(mission);
    }

    // 미션 승인/거절 (부모)
    @Transactional
    public MissionDto.MissionResponse reviewMission(Long missionId, MissionDto.ReviewRequest request) {
        DailyMissions mission = getMissionOrThrow(missionId);
        if ("APPROVED".equals(request.getStatus())) {
            mission.approve(LocalDateTime.now());
        } else if ("REJECTED".equals(request.getStatus())) {
            mission.reject(request.getRejectReason());
        } else {
            throw new CustomException(ErrorCode.INVALID_MISSION_STATUS);
        }
        return MissionDto.MissionResponse.from(mission);
    }

    private DailyMissions getMissionOrThrow(Long missionId) {
        return dailyMissionsRepository.findById(missionId)
                .orElseThrow(() -> new CustomException(ErrorCode.MISSION_NOT_FOUND));
    }

    /**
     * [스케줄러 전용] 부모님의 루틴 프리셋을 읽어서 '오늘의 미션'으로 일괄 복사
     */
    @Transactional
    public void generateDailyMissionsFromPresets() {
        log.info("🚀 [배치 작업 시작] 모든 아이들의 오늘자 미션 생성을 시작합니다.");
        LocalDate today = LocalDate.now();

        // 1. 우리 서비스에 가입된 모든 아이들을 불러온다.
        List<Children> allChildren = childrenRepository.findAll();

        for (Children child : allChildren) {
            Parents parent = child.getParent();
            if (parent == null) continue; // 호적 없는 아이는 패스!

            // 2. 이 아이의 부모님이 만들어둔 '루틴 프리셋' 목록을 다 가져온다.
            List<RoutinePresets> parentPresets = routinePresetsRepository.findByParentId(parent.getId());

            for (RoutinePresets preset : parentPresets) {
                // 3. 프리셋 안에 들어있는 '진짜 할 일(BigTasks)'을 순서대로 꺼낸다.
                List<PresetBigTasks> bigTasks = presetBigTasksRepository.findByPresetIdOrderByOrderIndex(preset.getId());

                for (PresetBigTasks bigTask : bigTasks) {
                    // 🚨 중복 생성 방지 로직 (선택이지만 필수급): 이미 오늘 똑같은 미션을 만들어줬는지 확인!
                    // (TODO: DailyMissionsRepository에 아이ID, 날짜, OriginBigTaskId로 찾는 쿼리 필요시 추가)

                    // 4. 새로운 '오늘의 미션' 객체를 조립!
                    DailyMissions newMission = DailyMissions.create(
                            child,
                            preset.getId(),
                            bigTask.getId(),
                            preset.getTitle(),
                            bigTask.getTitle(),
                            null, // 태그
                            20, // 임시 할당 경험치 (추후 난이도 등에 따라 기획 수정 필요!)
                            today,
                            bigTask.getStartTime(),
                            bigTask.getEndTime()
                    );

                    // 5. DB에 저장!
                    dailyMissionsRepository.save(newMission);
                }
            }
        }
        log.info("✅ [배치 작업 완료] 오늘의 미션 복사가 모두 끝났습니다!");
    }

    /**
     * 주간 미션 달성률 통계 API
     */
    @Transactional(readOnly = true)
    public MissionDto.StatisticsResponse getWeeklyStatistics(Long childId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(7); // 오늘 기준 최근 7일!

        // 1. 최근 7일 치 미션을 창고에서 싹 쓸어온다!
        List<DailyMissions> weeklyMissions = dailyMissionsRepository.findByChildIdAndDateBetween(childId, startOfWeek, today);

        // 2. 전체 미션 개수 파악
        int total = weeklyMissions.size();

        // 3. 그중에서 상태가 '완료(COMPLETED)'거나 부모님이 '승인(APPROVED)'한 미션만 필터링해서 개수 세기!
        int completed = (int) weeklyMissions.stream()
                .filter(m -> "COMPLETED".equals(m.getStatus()) || "APPROVED".equals(m.getStatus()))
                .count();

        // 4. 달성률 계산 (0으로 나누는 에러 방지 & 소수점 첫째 자리까지만 예쁘게 자르기)
        double rate = (total == 0) ? 0.0 : Math.round(((double) completed / total) * 1000) / 10.0;

        log.info("📊 통계 산출 완료 - childId: {}, 달성률: {}%", childId, rate);

        return MissionDto.StatisticsResponse.builder()
                .totalMissions(total)
                .completedMissions(completed)
                .completionRate(rate)
                .build();
    }
}
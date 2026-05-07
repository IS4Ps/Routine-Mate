package com.hansung.adhd.scheduler;

import com.hansung.adhd.service.MissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MissionScheduler {

    private final MissionService missionService;

    /**
     * 매일 자정(00:00:00)에 실행되는 미션 생성기
     * (크론 표현식: 초 분 시 일 월 요일)
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void generateDailyMissionsAtMidnight() {
        log.info("⏰ [자정 알람] 오늘의 미션 자동 생성 스케줄러가 작동했습니다!");
        missionService.generateDailyMissionsFromPresets();
    }

    // 테스트용 1분 스케줄러 — 발표 전 비활성화
//    @Scheduled(fixedRate = 60000)
//    public void testSchedulerForProfessor() {
//        log.info("⏰ [테스트 알람] 1분이 지났습니다! 미션이 자동 생성됩니다!");
//        missionService.generateDailyMissionsFromPresets();
//    }
}
package com.hansung.adhd.service;

import com.hansung.adhd.dto.MinigameDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 게임 세션 임시 저장소 (메모리)
 * 게임 시작 시 문제 세트를 저장하고 제출 시 채점에 사용
 */
@Component
public class MinigameSessionStore {

    private final Map<String, List<MinigameDto.Stimulus>> sessions = new ConcurrentHashMap<>();

    public void save(String sessionId, List<MinigameDto.Stimulus> stimuli) {
        sessions.put(sessionId, stimuli);
    }

    public List<MinigameDto.Stimulus> get(String sessionId) {
        return sessions.get(sessionId);
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
    }
}

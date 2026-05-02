package com.hansung.adhd.service;

import com.hansung.adhd.dto.MinigameDto;
import lombok.Getter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MinigameSessionStore {

    // Go/No-Go 세션
    private final Map<String, List<MinigameDto.Stimulus>> goNoGoSessions = new ConcurrentHashMap<>();

    // 스트룹 세션
    private final Map<String, List<MinigameDto.StroopStimulus>> stroopSessions = new ConcurrentHashMap<>();

    // N-Back 세션
    private final Map<String, NBackSession> nBackSessions = new ConcurrentHashMap<>();

    // ── Go/No-Go ─────────────────────────────────────────────────────────────
    public void save(String sessionId, List<MinigameDto.Stimulus> stimuli) {
        goNoGoSessions.put(sessionId, stimuli);
    }
    public List<MinigameDto.Stimulus> get(String sessionId) {
        return goNoGoSessions.get(sessionId);
    }
    public void remove(String sessionId) {
        goNoGoSessions.remove(sessionId);
    }

    // ── 스트룹 ────────────────────────────────────────────────────────────────
    public void saveStroop(String sessionId, List<MinigameDto.StroopStimulus> stimuli) {
        stroopSessions.put(sessionId, stimuli);
    }
    public List<MinigameDto.StroopStimulus> getStroop(String sessionId) {
        return stroopSessions.get(sessionId);
    }
    public void removeStroop(String sessionId) {
        stroopSessions.remove(sessionId);
    }

    // ── N-Back ────────────────────────────────────────────────────────────────
    public void saveNBack(String sessionId, List<MinigameDto.NBackStimulus> stimuli, int nLevel) {
        nBackSessions.put(sessionId, new NBackSession(stimuli, nLevel));
    }
    public NBackSession getNBack(String sessionId) {
        return nBackSessions.get(sessionId);
    }
    public void removeNBack(String sessionId) {
        nBackSessions.remove(sessionId);
    }

    @Getter
    @AllArgsConstructor
    public static class NBackSession {
        private List<MinigameDto.NBackStimulus> stimuli;
        private int nLevel;
    }
}
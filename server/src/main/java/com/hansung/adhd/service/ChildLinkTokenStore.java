package com.hansung.adhd.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChildLinkTokenStore {

    private static final long EXPIRY_SECONDS = 600; // 10분

    private record TokenEntry(Long childId, Instant expiresAt) {}

    private final Map<String, TokenEntry> store = new ConcurrentHashMap<>();

    public void save(String token, Long childId) {
        store.put(token, new TokenEntry(childId, Instant.now().plusSeconds(EXPIRY_SECONDS)));
    }

    public Long getChildId(String token) {
        TokenEntry entry = store.get(token);
        if (entry == null || Instant.now().isAfter(entry.expiresAt())) {
            store.remove(token);
            return null;
        }
        return entry.childId();
    }

    public void remove(String token) {
        store.remove(token);
    }
}

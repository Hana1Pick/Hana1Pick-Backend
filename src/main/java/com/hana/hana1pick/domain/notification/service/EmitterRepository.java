package com.hana.hana1pick.domain.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class EmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        log.info("Saving emitter with Id" + emitterId);
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    public void saveEventCache(String emitterId, Object event) {
        eventCache.put(emitterId, event);
    }

    public Map<String, SseEmitter> findAllEmitterStartWithByUserEmail(String email) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(email))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public Map<String, Object> findAllEventCacheStartWithByUserEmail(String email) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(email))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void deleteById(String id) {
        emitters.remove(id);
    }

    public void deleteAllEmitterStartWithUserEmail(String email) {
        emitters.forEach((key, emitter) -> {
            if (key.startsWith(email)) {
                emitters.remove(key);
            }
        });
    }

    public void deleteAllEventCacheStartWithUserEmail(String email) {
        eventCache.forEach((key, emitter) -> {
            if (key.startsWith(email)) {
                emitters.remove(key);
            }
        });
    }
}

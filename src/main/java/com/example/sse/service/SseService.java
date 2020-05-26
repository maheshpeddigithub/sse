package com.example.sse.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@EnableScheduling
public class SseService {

    final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public void addEmitter(final SseEmitter emitter) {
        emitters.add(emitter);
    }

    public void removeEmitter(final SseEmitter emitter) {
        emitters.remove(emitter);
    }

    @Async
    @Scheduled(fixedRate = 5000)
    public void doNotify() throws IOException {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        Random random = new Random();
        map.put("Info", random.nextInt(100));
        map.put("Warning", random.nextInt(50));
        map.put("Error", random.nextInt(25));
        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .data(map));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }

}

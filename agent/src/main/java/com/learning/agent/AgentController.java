package com.learning.agent;

import com.learning.common.AgentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final Orchestrator orchestrator;

    // 改为 SSE 流式输出
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat(@RequestBody Map<String, String> body) {
        String history = body.getOrDefault("history", "");
        String message = body.get("message");

        // 如果有历史记录，就拼接；如果没有，直接传当前问题
        String fullMessage;
        if (history == null || history.trim().isEmpty()) {
            fullMessage = message;
        } else {
            fullMessage = "对话历史：\n" + history + "\n当前提问：" + message;
        }

        return orchestrator.handleStream(fullMessage);
    }
}
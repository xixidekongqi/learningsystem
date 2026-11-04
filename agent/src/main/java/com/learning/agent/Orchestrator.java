package com.learning.agent;

import com.learning.common.AgentResponse;
import com.learning.common.RetrievedChunk;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Orchestrator {

    private final RetrievalService retrievalService;
    private final PlanningAgent planningAgent;
    private final ConsolidationAgent consolidationAgent;
    private final AssessmentAgent assessmentAgent;

    // 新增：流式处理方法
    public Flux<String> handleStream(String userInput) {
        List<RetrievedChunk> chunks = retrievalService.retrieve(userInput, 5);
        String intent = classify(userInput);

        if ("PLAN".equals(intent)) {
            return planningAgent.generateStream(userInput, chunks);
        } else if ("ASSESS".equals(intent)) {
            return assessmentAgent.generateStream(userInput, chunks);
        } else if ("MIXED".equals(intent)) {
            // 为了简单起见，MIXED 先合并输出
            return Flux.concat(
                    planningAgent.generateStream(userInput, chunks),
                    assessmentAgent.generateStream(userInput, chunks)
            );
        } else {
            return consolidationAgent.generateStream(userInput, chunks);
        }
    }

    // 原来的同步处理方法保留
    public List<AgentResponse> handle(String userInput) {
        // ... 保持你原来的代码不变 ...
        return new ArrayList<>();
    }

    private String classify(String input) {
        boolean plan = input.contains("计划") || input.contains("路径") || input.contains("安排");
        boolean assess = input.contains("测试") || input.contains("考试") || input.contains("出题");
        if (plan && assess) return "MIXED";
        if (plan) return "PLAN";
        if (assess) return "ASSESS";
        return "CONSOLIDATE";
    }
}
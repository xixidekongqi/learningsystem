package com.learning.agent;

import com.learning.common.RetrievedChunk;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Orchestrator {

    private final RetrievalService retrievalService;
    private final PlanningAgent planningAgent;
    private final ConsolidationAgent consolidationAgent;
    private final AssessmentAgent assessmentAgent;

    public Flux<String> handleStream(String userInput) {
        List<RetrievedChunk> chunks = retrievalService.retrieve(userInput, 5);
        String intent = classify(userInput);

        if ("PLAN".equals(intent)) {
            return planningAgent.generateStream(userInput, chunks);
        } else if ("ASSESS".equals(intent)) {
            return assessmentAgent.generateStream(userInput, chunks);
        } else if ("MIXED".equals(intent)) {
            // 核心：混合意图时，在文本中间加一个明显的物理分隔线
            return Flux.concat(
                    planningAgent.generateStream(userInput, chunks),
                    Flux.just("\n\n---我是分割线---\n\n"),
                    assessmentAgent.generateStream(userInput, chunks)
            );
        } else {
            return consolidationAgent.generateStream(userInput, chunks);
        }
    }

    private String classify(String input) {
        boolean plan = input.contains("计划") || input.contains("路径") || input.contains("安排");
        boolean assess = input.contains("测试") || input.contains("考试") || input.contains("出题") || input.contains("练习");
        if (plan && assess) return "MIXED";
        if (plan) return "PLAN";
        if (assess) return "ASSESS";
        return "CONSOLIDATE";
    }
}
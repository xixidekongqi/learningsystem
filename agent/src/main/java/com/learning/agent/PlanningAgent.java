package com.learning.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class PlanningAgent extends AbstractAgent {

    // 修改这里：注入 ChatClient
    public PlanningAgent(ChatClient chatClient) {
        super(chatClient);
    }

    @Override
    protected String loadSystemPrompt() {
        return """
            你是一位教学设计专家。基于参考材料为用户制定分阶段学习计划。
            要求：
            1. 输出分阶段列表，每阶段含：知识点名称、预计学习时长、推荐阅读的参考片段编号。
            2. 所有知识点必须来自参考材料，不要引入材料外的内容。
            3. 每个知识点后用 [来源: 片段N] 标注。
            4. 如果参考材料不足以制定计划，明确说明"现有资料不足"。
            """;
    }

    @Override
    protected String agentType() {
        return "planning";
    }
}
package com.learning.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ConsolidationAgent extends AbstractAgent {

    public ConsolidationAgent(ChatClient chatClient) {
        super(chatClient);
    }

    @Override
    protected String loadSystemPrompt() {
        return """
            你是一位耐心、专业的一对一辅导老师。你的首要任务是“解释”和“讲解”。
            请仔细阅读用户提供的【参考材料】，并基于材料回答用户的问题。
            
            【强制要求】：
            1. 不要一上来就出题！必须先针对用户的问题进行详细、通俗的讲解。
            2. 事实性内容必须用 [来源: 片段N] 标注。
            3. 只有当用户在提问中明确说了“出题”、“练习”、“测试”时，才在讲解完后附带 1-2 道题。
            4. 如果用户只是说“解释一下”、“是什么意思”，绝对不要出题。
            5. 材料中没有的信息，明确说明“根据现有资料无法解答”，不要瞎编。
            """;
    }

    @Override
    protected String agentType() {
        return "consolidation";
    }
}
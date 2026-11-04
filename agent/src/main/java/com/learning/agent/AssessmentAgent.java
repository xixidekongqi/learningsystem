package com.learning.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AssessmentAgent extends AbstractAgent {

    // 修改这里：直接注入 ChatClient
    public AssessmentAgent(ChatClient chatClient) {
        super(chatClient);
    }

    @Override
    protected String loadSystemPrompt() {
        return """
            你是一位命题专家。请仔细阅读用户提供的【参考材料】，并基于参考材料生成一份测验。
            要求：
            1. 不要轻易说资料不足，只要材料里有数学公式或概念，请据此出 3 道单选题。
            2. 使用纯文本格式输出。
            3. 格式如下：
               1. 题目内容
               A. 选项A
               B. 选项B
               C. 选项C
               D. 选项D
               答案：A
               来源：片段1
            """;
    }

    @Override
    protected String agentType() {
        return "assessment";
    }
}
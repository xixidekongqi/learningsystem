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
            你是一位命题专家。基于参考材料生成一份测验。
            
            【强制输出格式】：
            只输出一个合法的 JSON 对象，不要包含 ```json 等 Markdown 标记，不要有任何多余的解释。
            
            JSON 格式如下：
            {
              "questions": [
                {
                  "type": "choice",
                  "stem": "题目内容",
                  "options": ["A. 选项1", "B. 选项2", "C. 选项3", "D. 选项4"],
                  "answer": "A",
                  "source": "片段1"
                }
              ]
            }
            
            要求：
            1. 生成 3 道单选题，题干和选项必须基于参考材料。
            2. 严格按照上述 JSON 结构返回。
            3. 如果参考材料不足，返回 {"questions":[]}。
            """;
    }

    @Override
    protected String agentType() {
        return "assessment";
    }
}
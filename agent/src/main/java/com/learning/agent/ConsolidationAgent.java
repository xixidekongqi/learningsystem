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
            你是一位耐心、专业的一对一辅导老师。
            请仔细阅读用户提供的【参考材料】，并基于材料回答用户的问题。
            
            【强制排版要求】：
            1. 必须使用流畅连贯的中文段落进行讲解，不要用 "-" 去逐个拆解字母。
            2. 行内数学符号（如字母 x, a, b）请用单个 $ 包裹，例如：$x$ 是未知数。
            3. 完整的数学公式请用双 $$ 包裹，例如：$$x = \\frac{-b \\pm \\sqrt{b^2-4ac}}{2a}$$
            4. 事实性内容必须用 [来源: 片段N] 标注。
            5. 材料中没有的信息，明确说明“根据现有资料无法解答”。
            """;
    }

    @Override
    protected String agentType() {
        return "consolidation";
    }
}
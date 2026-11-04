package com.learning.agent;

import com.learning.common.AgentResponse;
import com.learning.common.RetrievedChunk;
import org.springframework.ai.chat.client.ChatClient;
import reactor.core.publisher.Flux;

import java.util.List;

public abstract class AbstractAgent {

    protected final ChatClient chatClient;

    protected AbstractAgent(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // 原来的同步生成（保留）
    public AgentResponse generate(String question, List<RetrievedChunk> chunks) {
        String context = buildContext(chunks);
        String content = chatClient.prompt()
                .system(loadSystemPrompt())
                .user(u -> u.text("参考材料：\n" + context + "\n\n用户问题：" + question))
                .call()
                .content();
        return new AgentResponse(agentType(), content, chunks);
    }

    // 流式生成（带控制台日志）
    public Flux<String> generateStream(String question, List<RetrievedChunk> chunks) {
        // ================= 新增的日志打印 =================
        System.out.println("\n============== [RAG 检索日志] 智能体类型: " + agentType() + " ==============");
        System.out.println("用户问题: " + question);
        System.out.println("检索到片段数量: " + chunks.size());
        for (int i = 0; i < chunks.size(); i++) {
            RetrievedChunk c = chunks.get(i);
            System.out.println(" -> 片段 " + (i + 1) + " [来源: " + c.getSourceTitle() + "]: " + c.getContent());
        }
        System.out.println("======================================================================\n");
        // =================================================

        String context = buildContext(chunks);

        return chatClient.prompt()
                .system(loadSystemPrompt())
                .user(u -> u.text("参考材料：\n" + context + "\n\n用户问题：" + question))
                .stream()
                .content(); // 返回 Flux<String> 流式数据
    }

    protected String buildContext(List<RetrievedChunk> chunks) {
        if (chunks.isEmpty()) {
            return "（当前没有检索到任何参考材料）";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chunks.size(); i++) {
            RetrievedChunk c = chunks.get(i);
            sb.append("【片段").append(i + 1).append("｜")
                    .append(c.getSourceTitle()).append("｜")
                    .append(c.getSection()).append("】\n")
                    .append(c.getContent()).append("\n\n");
        }
        return sb.toString();
    }

    protected abstract String loadSystemPrompt();
    protected abstract String agentType();
}
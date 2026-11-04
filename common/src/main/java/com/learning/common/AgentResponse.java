package com.learning.common;

import lombok.Data;
import java.util.List;

@Data
public class AgentResponse {
    private String agentType;
    private String content;
    private List<RetrievedChunk> references;

    public AgentResponse(String agentType, String content, List<RetrievedChunk> references) {
        this.agentType = agentType;
        this.content = content;
        this.references = references;
    }
}
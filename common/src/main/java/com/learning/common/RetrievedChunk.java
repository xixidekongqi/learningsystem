package com.learning.common;

import lombok.Data;

@Data
public class RetrievedChunk {
    private String chunkId;
    private String content;
    private String sourceTitle;
    private String section;
    private double score;
}
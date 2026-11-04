package com.learning.agent;

import com.learning.common.RetrievedChunk;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetrievalService {

    private final VectorStore vectorStore;

    public List<RetrievedChunk> retrieve(String query, int topK) {
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.query(query).withTopK(topK)
        );
        return docs.stream().map(d -> {
            RetrievedChunk c = new RetrievedChunk();
            c.setChunkId(d.getId());
            c.setContent(d.getContent());
            c.setSourceTitle((String) d.getMetadata().getOrDefault("sourceTitle", "未知"));
            c.setSection((String) d.getMetadata().getOrDefault("section", ""));
            c.setScore(0.0);
            return c;
        }).collect(Collectors.toList());
    }
}
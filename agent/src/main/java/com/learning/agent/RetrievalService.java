package com.learning.agent;

import com.learning.common.RetrievedChunk;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RetrievalService {

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    public List<RetrievedChunk> retrieve(String query, int topK) {
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.query(query).withTopK(topK * 2)
        );

        return docs.stream().map(d -> {
                    RetrievedChunk c = new RetrievedChunk();
                    c.setChunkId(d.getId());
                    c.setContent(d.getContent());
                    c.setSourceTitle((String) d.getMetadata().getOrDefault("sourceTitle", "未知"));
                    c.setSection((String) d.getMetadata().getOrDefault("section", ""));
                    c.setScore(0.8);

                    try {
                        String sql = "SELECT s.publish_date, c.status FROM document_chunk c " +
                                "JOIN source_document s ON c.document_id = s.id " +
                                "WHERE c.vector_id = ?";

                        var result = jdbcTemplate.queryForMap(sql, d.getId());

                        String status = (String) result.get("status");
                        if (!"VERIFIED".equals(status)) {
                            return null;
                        }

                        java.sql.Date publishDate = (java.sql.Date) result.get("publish_date");
                        if (publishDate != null) {
                            LocalDate docDate = publishDate.toLocalDate();
                            long monthsBetween = ChronoUnit.MONTHS.between(docDate, LocalDate.now());
                            if (monthsBetween > 0) {
                                double decayFactor = Math.pow(0.98, monthsBetween);
                                c.setScore(c.getScore() * decayFactor);
                            }
                        }
                    } catch (Exception e) {
                        c.setScore(0.5);
                    }
                    return c;
                })
                .filter(c -> c != null)
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(topK)
                .collect(Collectors.toList());
    }
}
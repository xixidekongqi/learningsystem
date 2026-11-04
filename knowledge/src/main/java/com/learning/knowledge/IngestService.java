package com.learning.knowledge;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class IngestService {

    private final TextSplitter splitter;
    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    public Long ingest(MultipartFile file) throws Exception {
        String title = file.getOriginalFilename();

        jdbcTemplate.update(
                "INSERT INTO source_document(title, file_path, status) VALUES (?, ?, ?)",
                title, "upload/" + title, "PROCESSING"
        );
        Long docId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);

        String text;
        try (InputStream in = file.getInputStream()) {
            text = new Tika().parseToString(in);
        }

        List<String> chunks = splitter.split(text);
        List<Document> docs = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            String chunk = chunks.get(i);
            Map<String, Object> meta = new HashMap<>();
            meta.put("documentId", docId);
            meta.put("chunkIndex", i);
            meta.put("sourceTitle", title);
            meta.put("section", "第" + (i + 1) + "段");

            Document doc = new Document(chunk, meta);
            docs.add(doc);

            jdbcTemplate.update(
                    "INSERT INTO document_chunk(document_id, chunk_index, content, vector_id, metadata) VALUES (?, ?, ?, ?, ?)",
                    docId, i, chunk, doc.getId(), JSON.toJSONString(meta)
            );
        }

        vectorStore.add(docs);

        jdbcTemplate.update("UPDATE source_document SET status = ? WHERE id = ?", "DONE", docId);
        return docId;
    }
}
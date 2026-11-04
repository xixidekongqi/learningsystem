CREATE TABLE IF NOT EXISTS source_document (
                                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                               title VARCHAR(255),
    file_path VARCHAR(500),
    status VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS document_chunk (
                                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                              document_id BIGINT,
                                              chunk_index INT,
                                              content TEXT,
                                              vector_id VARCHAR(100),
    metadata JSON
    );
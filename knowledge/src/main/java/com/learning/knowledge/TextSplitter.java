package com.learning.knowledge;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class TextSplitter {

    private static final int MAX_LEN = 500;
    private static final int OVERLAP = 50;

    public List<String> split(String text) {
        List<String> result = new ArrayList<>();
        if (text == null || text.isBlank()) return result;

        String[] paragraphs = text.split("\\n\\s*\\n");
        StringBuilder buffer = new StringBuilder();

        for (String p : paragraphs) {
            p = p.trim();
            if (p.isEmpty()) continue;

            if (p.length() > MAX_LEN) {
                if (buffer.length() > 0) {
                    result.add(buffer.toString());
                    buffer.setLength(0);
                }
                for (int i = 0; i < p.length(); i += (MAX_LEN - OVERLAP)) {
                    int end = Math.min(i + MAX_LEN, p.length());
                    result.add(p.substring(i, end));
                    if (end == p.length()) break;
                }
            } else if (buffer.length() + p.length() > MAX_LEN) {
                result.add(buffer.toString());
                String prev = buffer.toString();
                String tail = prev.length() > OVERLAP ? prev.substring(prev.length() - OVERLAP) : prev;
                buffer.setLength(0);
                buffer.append(tail).append("\n").append(p);
            } else {
                if (buffer.length() > 0) buffer.append("\n\n");
                buffer.append(p);
            }
        }
        if (buffer.length() > 0) result.add(buffer.toString());
        return result;
    }
}
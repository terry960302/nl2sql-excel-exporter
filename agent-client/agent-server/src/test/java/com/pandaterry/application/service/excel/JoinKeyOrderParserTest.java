package com.pandaterry.application.service.excel;

import com.pandaterry.application.service.excel.JoinKeyOrderParser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JoinKeyOrderParserTest {

    @Test
    void parseJoinKeys_simpleQuery() {
        String sql = "SELECT a.id, b.id FROM table_a a JOIN table_b b ON a.id = b.a_id JOIN table_c c ON b.id = c.b_id";
        JoinKeyOrderParser parser = new JoinKeyOrderParser();
        List<String> keys = parser.parseJoinKeys(sql);
        assertThat(keys).containsExactly("a.id", "b.id");
    }
}

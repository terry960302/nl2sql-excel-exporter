package com.pandaterry.application.service.excel;

import com.pandaterry.application.service.datasource.DataSourceManager;
import com.pandaterry.application.vo.CellRange;
import com.pandaterry.application.vo.FlatRow;
import com.pandaterry.domain.model.datasource.DatasourceSession;
import com.pandaterry.msa_contracts.enums.schema.DatabaseEngineType;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.inject.Inject;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@MicronautTest
@Tag("Docker")
class ExcelHierarchyExporterDbIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Inject
    private DataSourceManager dataSourceManager;

    private DatasourceSession session;

    @BeforeEach
    void setup() throws Exception {
        session = DatasourceSession.create(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword(),
                DatabaseEngineType.POSTGRESQL
        );
        dataSourceManager.register(session);
        dataSourceManager.testConnection(session.getDatasourceId());
        prepareTables();
        insertMockData();
    }

    private void prepareTables() throws Exception {
        try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS employee, team, department, company CASCADE");
            stmt.execute("""
                    CREATE TABLE company (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(50),
                        attr1 VARCHAR(50),
                        attr2 VARCHAR(50)
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE department (
                        id SERIAL PRIMARY KEY,
                        company_id INTEGER REFERENCES company(id),
                        name VARCHAR(50),
                        attr3 VARCHAR(50)
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE team (
                        id SERIAL PRIMARY KEY,
                        department_id INTEGER REFERENCES department(id),
                        name VARCHAR(50),
                        attr4 VARCHAR(50)
                    )
                    """);
            stmt.execute("""
                    CREATE TABLE employee (
                        id SERIAL PRIMARY KEY,
                        team_id INTEGER REFERENCES team(id),
                        name VARCHAR(50),
                        attr5 VARCHAR(50)
                    )
                    """);
        }
    }

    private void insertMockData() throws Exception {
        try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            for (int c = 0; c < 2; c++) {
                int companyId;
                try (PreparedStatement ps = conn.prepareStatement("INSERT INTO company(name, attr1, attr2) VALUES (?, ?, ?) RETURNING id")) {
                    ps.setString(1, "C" + c);
                    ps.setString(2, "CA" + c);
                    ps.setString(3, "CB" + c);
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    companyId = rs.getInt(1);
                }
                for (int d = 0; d < 3; d++) {
                    int deptId;
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO department(company_id, name, attr3) VALUES (?, ?, ?) RETURNING id")) {
                        ps.setInt(1, companyId);
                        ps.setString(2, "D" + d);
                        ps.setString(3, "DA" + d);
                        ResultSet rs = ps.executeQuery();
                        rs.next();
                        deptId = rs.getInt(1);
                    }
                    for (int t = 0; t < 4; t++) {
                        int teamId;
                        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO team(department_id, name, attr4) VALUES (?, ?, ?) RETURNING id")) {
                            ps.setInt(1, deptId);
                            ps.setString(2, "T" + t);
                            ps.setString(3, "TA" + t);
                            ResultSet rs = ps.executeQuery();
                            rs.next();
                            teamId = rs.getInt(1);
                        }
                        for (int e = 0; e < 5; e++) {
                            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO employee(team_id, name, attr5) VALUES (?, ?, ?)")) {
                                ps.setInt(1, teamId);
                                ps.setString(2, "E" + e);
                                ps.setString(3, "EA" + e);
                                ps.executeUpdate();
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    @DisplayName("DB에서 추출한 4단계 계층 데이터도 엑셀 병합 가능해야 한다")
    void export_from_database() throws Exception {
        String sql = """
                SELECT
                    c.id   AS \"c.id\", c.name AS \"c.name\", c.attr1 AS \"c.attr1\", c.attr2 AS \"c.attr2\",
                    d.id   AS \"d.id\", d.name AS \"d.name\", d.attr3 AS \"d.attr3\",
                    t.id   AS \"t.id\", t.name AS \"t.name\", t.attr4 AS \"t.attr4\",
                    e.id   AS \"e.id\", e.name AS \"e.name\", e.attr5 AS \"e.attr5\"
                FROM company c
                JOIN department d ON c.id = d.company_id
                JOIN team t ON d.id = t.department_id
                JOIN employee e ON t.id = e.team_id
                ORDER BY c.id, d.id, t.id, e.id
                """;

        List<String> columns = List.of(
                "c.id", "c.name", "c.attr1", "c.attr2",
                "d.id", "d.name", "d.attr3",
                "t.id", "t.name", "t.attr4",
                "e.id", "e.name", "e.attr5"
        );

        List<FlatRow> rows = new ArrayList<>();
        try (Connection conn = dataSourceManager.getConnection(session.getDatasourceId());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                FlatRow r = new FlatRow();
                r.getColumns().put("c.id", rs.getInt("c.id"));
                r.getColumns().put("c.name", rs.getString("c.name"));
                r.getColumns().put("c.attr1", rs.getString("c.attr1"));
                r.getColumns().put("c.attr2", rs.getString("c.attr2"));
                r.getColumns().put("d.id", rs.getInt("d.id"));
                r.getColumns().put("d.name", rs.getString("d.name"));
                r.getColumns().put("d.attr3", rs.getString("d.attr3"));
                r.getColumns().put("t.id", rs.getInt("t.id"));
                r.getColumns().put("t.name", rs.getString("t.name"));
                r.getColumns().put("t.attr4", rs.getString("t.attr4"));
                r.getColumns().put("e.id", rs.getInt("e.id"));
                r.getColumns().put("e.name", rs.getString("e.name"));
                r.getColumns().put("e.attr5", rs.getString("e.attr5"));
                rows.add(r);
            }
        }

        MergeRegionCalculator calc = new MergeRegionCalculator();
        Map<Integer, List<CellRange>> mergeMap = calc.calculateMergeRegions(rows, columns);

        assertThat(rows).hasSize(120);
        assertThat(mergeMap.get(0)).hasSize(2);
        assertThat(mergeMap.get(0).get(0)).isEqualTo(new CellRange(2, 61));
        assertThat(mergeMap.get(0).get(1)).isEqualTo(new CellRange(62, 121));
        assertThat(mergeMap.get(4)).hasSize(6);
        mergeMap.get(4).forEach(r -> assertThat(r.end() - r.start() + 1).isEqualTo(20));
        assertThat(mergeMap.get(7)).hasSize(24);
        mergeMap.get(7).forEach(r -> assertThat(r.end() - r.start() + 1).isEqualTo(5));
        assertThat(mergeMap.getOrDefault(10, List.of())).isEmpty();

        Path outDir = Paths.get("src/test/resources/output");
        Files.createDirectories(outDir);
        Path outFile = outDir.resolve("db_hierarchy.xlsx");
        try (OutputStream os = Files.newOutputStream(outFile)) {
            ExcelHierarchyExporter exporter = new ExcelHierarchyExporter();
            exporter.export(rows, columns, os);
        }

        assertThat(Files.exists(outFile)).isTrue();
        assertThat(Files.size(outFile)).isGreaterThan(0);

        try (Workbook wb = WorkbookFactory.create(Files.newInputStream(outFile))) {
            Sheet sheet = wb.getSheetAt(0);
            Map<Integer, Integer> mergedCount = new HashMap<>();
            for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
                CellRangeAddress addr = sheet.getMergedRegion(i);
                mergedCount.merge(addr.getFirstColumn(), 1, Integer::sum);
            }
            assertThat(mergedCount.get(0)).isEqualTo(2);
            assertThat(mergedCount.get(4)).isEqualTo(6);
            assertThat(mergedCount.get(7)).isEqualTo(24);
            assertThat(mergedCount.get(10)).isNull();
        }
    }
}


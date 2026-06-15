package com.leathercraft.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class DatabaseMigrationConfig implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationConfig.class);

    private static final String MIGRATION_SCRIPT = "db/migration/V1__add_material_summary.sql";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            ClassPathResource resource = new ClassPathResource(MIGRATION_SCRIPT);
            if (!resource.exists()) {
                log.warn("Migration script not found: {}", MIGRATION_SCRIPT);
                return;
            }
            try (InputStream is = resource.getInputStream()) {
                String sql = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
                String[] statements = splitStatements(sql);
                for (String stmt : statements) {
                    if (stmt != null && !stmt.trim().isEmpty()) {
                        jdbcTemplate.execute(stmt.trim());
                    }
                }
                log.info("Database migration executed successfully: {}", MIGRATION_SCRIPT);
            }
        } catch (Exception e) {
            log.warn("Database migration skipped or failed (may already be applied): {}", e.getMessage());
        }
    }

    private String[] splitStatements(String sql) {
        return sql.split(";\\s*(?=(?:[^']*'[^']*')*[^']*$)");
    }
}

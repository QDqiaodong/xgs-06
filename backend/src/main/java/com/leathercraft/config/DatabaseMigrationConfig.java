package com.leathercraft.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;

@Component
public class DatabaseMigrationConfig implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigrationConfig.class);

    private static final String MIGRATION_PATTERN = "db/migration/V*.sql";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            org.springframework.core.io.Resource[] resources = resolver.getResources("classpath:" + MIGRATION_PATTERN);

            if (resources == null || resources.length == 0) {
                log.warn("No migration scripts found");
                return;
            }

            Arrays.sort(resources, Comparator.comparing(r -> r.getFilename()));

            for (org.springframework.core.io.Resource resource : resources) {
                String fileName = resource.getFilename();
                try (InputStream is = resource.getInputStream()) {
                    String sql = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
                    String[] statements = splitStatements(sql);
                    for (String stmt : statements) {
                        if (stmt != null && !stmt.trim().isEmpty()) {
                            jdbcTemplate.execute(stmt.trim());
                        }
                    }
                    log.info("Database migration executed successfully: {}", fileName);
                } catch (Exception e) {
                    log.warn("Migration skipped or failed (may already be applied): {} - {}", fileName, e.getMessage());
                }
            }
        } catch (Exception e) {
            log.warn("Database migration process failed: {}", e.getMessage());
        }
    }

    private String[] splitStatements(String sql) {
        return sql.split(";\\s*(?=(?:[^']*'[^']*')*[^']*$)");
    }
}

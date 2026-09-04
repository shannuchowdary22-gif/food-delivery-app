package com.zomiggy.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class DatabaseConfigTest {

    @Autowired
    private Environment environment;

    @Test
    void datasourceUsesTlsRequiredByDefault() {
        String datasourceUrl = environment.getProperty("spring.datasource.url");
        assertNotNull(datasourceUrl, "Datasource URL must be configured");
        assertTrue(datasourceUrl.contains("sslMode=REQUIRED") || datasourceUrl.contains("sslMode=VERIFY_IDENTITY"),
                "Production datasource must require TLS for Aiven MySQL, got: " + datasourceUrl);
    }
}

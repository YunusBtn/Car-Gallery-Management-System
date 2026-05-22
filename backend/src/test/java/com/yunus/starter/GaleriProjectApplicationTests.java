package com.yunus.starter;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Integration test — çalışması için PostgreSQL veritabanı gereklidir.
 * Unit test koşumlarında @Disabled ile atlanır.
 * Docker Compose ile veritabanı ayakta olduğunda @Disabled kaldırılabilir.
 */
@Disabled("Integration test: PostgreSQL bağlantısı gerektirir. docker-compose up ile çalıştırın.")
@SpringBootTest
class GaleriProjectApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void printHibernateVersion() {
        String hibernateVersion = org.hibernate.Version.getVersionString();
        System.out.println("Hibernate Version: " + hibernateVersion);
    }

}

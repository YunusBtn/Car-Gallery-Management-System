package com.yunus.starter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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

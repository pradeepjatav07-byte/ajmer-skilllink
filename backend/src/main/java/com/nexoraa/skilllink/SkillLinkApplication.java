package com.nexoraa.skilllink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SkillLinkApplication {
    public static void main(String[] args) {
        SpringApplication.run(SkillLinkApplication.class, args);
    }
}

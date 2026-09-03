package com.example.volunteer_system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.example.volunteer_system.mapper")
@EnableScheduling
@EnableCaching
public class VolunteerSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(VolunteerSystemApplication.class, args);
    }

}

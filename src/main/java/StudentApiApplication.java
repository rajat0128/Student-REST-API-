package com.devops.studentapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication combines:
//   @Configuration  - marks this as a configuration class
//   @EnableAutoConfiguration - auto-configures Spring based on classpath
//   @ComponentScan  - scans for @Controller, @Service, @Repository beans

@SpringBootApplication
public class StudentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentApiApplication.class, args);
        System.out.println("===========================================");
        System.out.println("  Student API is running on port 8080");
        System.out.println("  Health: http://localhost:8080/actuator/health");
        System.out.println("  API:    http://localhost:8080/api/students");
        System.out.println("===========================================");
    }
}

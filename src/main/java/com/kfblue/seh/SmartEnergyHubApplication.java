package com.kfblue.seh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.kfblue.seh.mapper")
@EnableScheduling
public class SmartEnergyHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartEnergyHubApplication.class, args);
    }

}

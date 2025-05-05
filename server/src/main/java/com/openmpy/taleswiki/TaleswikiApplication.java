package com.openmpy.taleswiki;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class TaleswikiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaleswikiApplication.class, args);
    }

}

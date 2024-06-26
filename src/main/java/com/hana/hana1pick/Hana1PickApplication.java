package com.hana.hana1pick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class Hana1PickApplication {

    public static void main(String[] args) {
        SpringApplication.run(Hana1PickApplication.class, args);
    }

}

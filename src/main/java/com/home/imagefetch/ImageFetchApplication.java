package com.home.imagefetch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@EnableConfigurationProperties(AnimalProperties.class)
public class ImageFetchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageFetchApplication.class, args);
    }

    @Bean
    RestClient restClient() {
        return RestClient.create();
    }

}

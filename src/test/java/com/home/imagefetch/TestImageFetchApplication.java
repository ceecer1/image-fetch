package com.home.imagefetch;

import org.springframework.boot.SpringApplication;

public class TestImageFetchApplication {

    public static void main(String[] args) {
        SpringApplication.from(ImageFetchApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

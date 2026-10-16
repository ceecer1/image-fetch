package com.home.imagefetch;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ImageFetchApplicationTests {

    @Test
    void contextLoads() {
    }

}

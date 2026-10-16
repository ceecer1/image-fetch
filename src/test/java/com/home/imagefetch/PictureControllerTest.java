package com.home.imagefetch;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/** Web-layer contract test — no Spring context or database required. */
class PictureControllerTest {

    private final ImageFetchService service = mock(ImageFetchService.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PictureController(service)).build();
    }

    @Test
    void fetchReturnsSavedSummaries() throws Exception {
        AnimalPicture picture = new AnimalPicture("cat", "image/png", new byte[] {1, 2, 3});
        when(service.fetchAndSave(eq("cat"), anyInt())).thenReturn(List.of(picture, picture));

        mockMvc.perform(post("/api/pictures/cat").param("count", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].animalType").value("cat"))
                .andExpect(jsonPath("$[0].contentType").value("image/png"));
    }

    @Test
    void lastReturnsImageBytes() throws Exception {
        when(service.lastPicture("cat"))
                .thenReturn(Optional.of(new AnimalPicture("cat", "image/png", new byte[] {1, 2, 3})));

        mockMvc.perform(get("/api/pictures/cat/last"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(new byte[] {1, 2, 3}));
    }

    @Test
    void lastReturns404WhenNonePresent() throws Exception {
        when(service.lastPicture("dog")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pictures/dog/last")).andExpect(status().isNotFound());
    }

    @Test
    void unsupportedTypeReturns400() throws Exception {
        when(service.fetchAndSave(eq("zebra"), anyInt()))
                .thenThrow(new IllegalArgumentException("Unsupported animal type 'zebra'"));

        mockMvc.perform(post("/api/pictures/zebra")).andExpect(status().isBadRequest());
    }
}

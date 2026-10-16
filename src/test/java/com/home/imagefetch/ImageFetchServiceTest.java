package com.home.imagefetch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

/** Unit test for the fetch/save logic with the external provider stubbed. */
class ImageFetchServiceTest {

    private AnimalPictureRepository repository;
    private MockRestServiceServer server;
    private ImageFetchService service;

    @BeforeEach
    void setUp() {
        repository = org.mockito.Mockito.mock(AnimalPictureRepository.class);
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        AnimalProperties properties = new AnimalProperties(Map.of(
                "cat", "https://example.test/cat",
                "dog", "https://example.test/{w}/{h}"));
        service = new ImageFetchService(repository, builder.build(), properties);
    }

    @Test
    void fetchAndSavePersistsEachPicture() {
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));
        byte[] body = {1, 2, 3};
        server.expect(ExpectedCount.times(2), requestTo("https://example.test/cat"))
                .andRespond(withSuccess(body, MediaType.IMAGE_JPEG));

        var saved = service.fetchAndSave("CAT", 2);

        assertThat(saved).hasSize(2);
        ArgumentCaptor<AnimalPicture> captor = ArgumentCaptor.forClass(AnimalPicture.class);
        verify(repository, org.mockito.Mockito.times(2)).save(captor.capture());
        assertThat(captor.getValue().getAnimalType()).isEqualTo("cat");
        assertThat(captor.getValue().getContentType()).isEqualTo(MediaType.IMAGE_JPEG_VALUE);
        assertThat(captor.getValue().getData()).isEqualTo(body);
        server.verify();
    }

    @Test
    void unknownTypeThrows() {
        assertThatIllegalArgumentException().isThrownBy(() -> service.fetchAndSave("zebra", 1));
    }

    @Test
    void countBelowOneThrows() {
        assertThatIllegalArgumentException().isThrownBy(() -> service.fetchAndSave("cat", 0));
    }
}

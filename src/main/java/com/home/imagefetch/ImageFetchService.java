package com.home.imagefetch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ImageFetchService {

    private final AnimalPictureRepository repository;
    private final RestClient restClient;
    private final AnimalProperties properties;

    public ImageFetchService(AnimalPictureRepository repository, RestClient restClient,
                             AnimalProperties properties) {
        this.repository = repository;
        this.restClient = restClient;
        this.properties = properties;
    }

    /**
     * Fetches {@code count} pictures of the given animal type from the configured
     * provider and persists them.
     */
    public List<AnimalPicture> fetchAndSave(String animalType, int count) {
        String type = animalType.toLowerCase();
        String template = properties.sources().get(type);
        if (template == null) {
            throw new IllegalArgumentException(
                    "Unsupported animal type '" + animalType + "'. Supported: " + properties.sources().keySet());
        }
        if (count < 1) {
            throw new IllegalArgumentException("count must be at least 1");
        }

        List<AnimalPicture> saved = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            saved.add(repository.save(fetch(type, template)));
        }
        return saved;
    }

    public Optional<AnimalPicture> lastPicture(String animalType) {
        return repository.findFirstByAnimalTypeOrderByCreatedAtDescIdDesc(animalType.toLowerCase());
    }

    private AnimalPicture fetch(String type, String template) {
        String url = template
                .replace("{w}", Integer.toString(ThreadLocalRandom.current().nextInt(200, 600)))
                .replace("{h}", Integer.toString(ThreadLocalRandom.current().nextInt(200, 600)));

        ResponseEntity<byte[]> response = restClient.get().uri(url).retrieve().toEntity(byte[].class);
        byte[] data = response.getBody();
        if (data == null || data.length == 0) {
            throw new IllegalStateException("Image provider returned an empty body for " + url);
        }
        MediaType contentType = response.getHeaders().getContentType();
        return new AnimalPicture(type, contentType != null ? contentType.toString() : MediaType.IMAGE_JPEG_VALUE, data);
    }
}

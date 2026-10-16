package com.home.imagefetch;

import java.time.Instant;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pictures")
public class PictureController {

    private final ImageFetchService service;

    public PictureController(ImageFetchService service) {
        this.service = service;
    }

    /** Metadata view of a stored picture (never exposes the raw bytes). */
    public record PictureSummary(Long id, String animalType, String contentType, Instant createdAt) {
        static PictureSummary of(AnimalPicture picture) {
            return new PictureSummary(picture.getId(), picture.getAnimalType(),
                    picture.getContentType(), picture.getCreatedAt());
        }
    }

    /** Fetch {@code count} pictures of {@code type} from the provider and save them. */
    @PostMapping("/{type}")
    public List<PictureSummary> fetch(@PathVariable String type,
                                      @RequestParam(defaultValue = "1") int count) {
        return service.fetchAndSave(type, count).stream().map(PictureSummary::of).toList();
    }

    /** Return the raw bytes of the most recently stored picture of {@code type}. */
    @GetMapping("/{type}/last")
    public ResponseEntity<byte[]> last(@PathVariable String type) {
        return service.lastPicture(type)
                .map(picture -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(picture.getContentType()))
                        .body(picture.getData()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException ex) {
        return ex.getMessage();
    }
}

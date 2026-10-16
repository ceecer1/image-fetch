package com.home.imagefetch;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Maps an animal type (e.g. {@code cat}) to the URL template of the image
 * provider. The templates may contain {@code {w}} and {@code {h}} placeholders
 * which are replaced with random dimensions to vary the returned picture.
 */
@ConfigurationProperties(prefix = "animal")
public record AnimalProperties(Map<String, String> sources) {
}

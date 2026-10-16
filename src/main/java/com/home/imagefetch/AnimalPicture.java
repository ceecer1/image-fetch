package com.home.imagefetch;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;

@Entity
public class AnimalPicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String animalType;

    private String contentType;

    @Column(columnDefinition = "bytea")
    private byte[] data;

    private Instant createdAt;

    protected AnimalPicture() {
    }

    public AnimalPicture(String animalType, String contentType, byte[] data) {
        this.animalType = animalType;
        this.contentType = contentType;
        this.data = data;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getAnimalType() {
        return animalType;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getData() {
        return data;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

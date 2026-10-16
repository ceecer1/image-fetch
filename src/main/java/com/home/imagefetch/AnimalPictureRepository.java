package com.home.imagefetch;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalPictureRepository extends JpaRepository<AnimalPicture, Long> {

    Optional<AnimalPicture> findFirstByAnimalTypeOrderByCreatedAtDescIdDesc(String animalType);
}

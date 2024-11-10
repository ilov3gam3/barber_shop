package org.example.barber_shop.Repository;

import org.example.barber_shop.Entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
    File findByName(String name);
}

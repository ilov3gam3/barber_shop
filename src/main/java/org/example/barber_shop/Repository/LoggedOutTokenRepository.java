package org.example.barber_shop.Repository;

import org.example.barber_shop.Entity.LoggedOutToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoggedOutTokenRepository extends JpaRepository<LoggedOutToken, Long> {
    LoggedOutToken findByToken(String token);
}

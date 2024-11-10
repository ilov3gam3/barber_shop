package org.example.barber_shop.Repository;

import org.example.barber_shop.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByEmailOrPhone(String email, String phone);
    User findByToken(String token);
}

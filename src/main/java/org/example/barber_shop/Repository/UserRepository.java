package org.example.barber_shop.Repository;

import org.example.barber_shop.Constants.Role;
import org.example.barber_shop.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByPhone(String phone);
    User findByEmailOrPhone(String email, String phone);
    User findByToken(String token);
    List<User> findAllByRole(Role role);
    User findByIdAndRole(Long id, Role role);
    User findByName(String name);
}

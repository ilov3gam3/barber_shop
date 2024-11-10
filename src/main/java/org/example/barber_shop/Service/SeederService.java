package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.Role;
import org.example.barber_shop.Entity.File;
import org.example.barber_shop.Entity.User;
import org.example.barber_shop.Repository.FileRepository;
import org.example.barber_shop.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
@RequiredArgsConstructor
public class SeederService implements CommandLineRunner {
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
        if (fileRepository.count() == 0){
            seedDefaultAvatar();
        }
        if (userRepository.count() == 0){
            seedDefaultUsers();
        }
    }
    public void seedDefaultAvatar(){
        File file = new File();
        file.setName("default-avatar");
        file.setUrl("https://i.ibb.co/2c4kH3b/default-avatar.png");
        file.setThumbUrl("https://i.ibb.co/VM4q5YK/default-avatar.png");
        file.setMediumUrl("https://i.ibb.co/VM4q5YK/default-avatar.png");
        file.setDeleteUrl("https://ibb.co/VM4q5YK/0b168f35a20f4271ad76989a1ddbc532");
        file.setOwner(null);
        fileRepository.save(file);
    }
    public void seedDefaultUsers(){
        User user1 = new User();
        user1.setName("admin");
        user1.setDob(new Date(System.currentTimeMillis()));
        user1.setPhone("0111111111");
        user1.setEmail("admin@gmail.com");
        user1.setPassword(passwordEncoder.encode("123456"));
        user1.setVerified(true);
        user1.setBlocked(false);
        user1.setAvatar(fileRepository.findByName("default-avatar"));
        user1.setRole(Role.ROLE_ADMIN);
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("customer");
        user2.setDob(new Date(System.currentTimeMillis()));
        user2.setPhone("0222222222");
        user2.setEmail("customer@gmail.com");
        user2.setPassword(passwordEncoder.encode("123456"));
        user2.setVerified(true);
        user2.setBlocked(false);
        user2.setAvatar(fileRepository.findByName("default-avatar"));
        user2.setRole(Role.ROLE_CUSTOMER);
        userRepository.save(user2);

        User user3 = new User();
        user3.setName("staff");
        user3.setDob(new Date(System.currentTimeMillis()));
        user3.setPhone("0333333333");
        user3.setEmail("staff@gmail.com");
        user3.setPassword(passwordEncoder.encode("123456"));
        user3.setVerified(true);
        user3.setBlocked(false);
        user3.setAvatar(fileRepository.findByName("default-avatar"));
        user3.setRole(Role.ROLE_STAFF);
        userRepository.save(user3);

        User user4 = new User();
        user4.setName("receptionist");
        user4.setDob(new Date(System.currentTimeMillis()));
        user4.setPhone("0444444444");
        user4.setEmail("receptionist@gmail.com");
        user4.setPassword(passwordEncoder.encode("123456"));
        user4.setVerified(true);
        user4.setBlocked(false);
        user4.setAvatar(fileRepository.findByName("default-avatar"));
        user4.setRole(Role.ROLE_RECEPTIONIST);
        userRepository.save(user4);
    }
}

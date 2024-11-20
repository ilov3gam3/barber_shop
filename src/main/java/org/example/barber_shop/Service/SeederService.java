package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.Role;
import org.example.barber_shop.Entity.*;
import org.example.barber_shop.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeederService implements CommandLineRunner {
    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceRepository serviceRepository;
    private final ComboRepository comboRepository;
    private final ShiftRepository shiftRepository;
    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0){
            seedDefaultUsers();
        }
        if (serviceTypeRepository.count() == 0 && comboRepository.count() == 0) {
            seedDefaultTypesServicesCombos();
        }
        if (shiftRepository.count() == 0) {
            Shift morning = new Shift();
            morning.setName("Morning shift");
            morning.setStartTime(LocalTime.of(7, 0));
            morning.setEndTime(LocalTime.of(12, 0));

            Shift afternoon = new Shift();
            afternoon.setName("Afternoon shift");
            afternoon.setStartTime(LocalTime.of(12, 0));
            afternoon.setEndTime(LocalTime.of(17, 0));

            Shift night = new Shift();
            night.setName("Night shift");
            night.setStartTime(LocalTime.of(17, 0));
            night.setEndTime(LocalTime.of(22, 0));

            shiftRepository.saveAll(List.of(morning, afternoon, night));
        }
        System.out.println("============seed done============");
    }
    public void seedDefaultUsers(){
        File file = new File();
        file.setName("default-avatar");
        file.setUrl("https://i.ibb.co/2c4kH3b/default-avatar.png");
        file.setThumbUrl("https://i.ibb.co/VM4q5YK/default-avatar.png");
        file.setMediumUrl("https://i.ibb.co/VM4q5YK/default-avatar.png");
        file.setDeleteUrl("https://ibb.co/VM4q5YK/0b168f35a20f4271ad76989a1ddbc532");
        file.setOwner(null);
        file = fileRepository.save(file);

        User user1 = new User();
        user1.setName("admin");
        user1.setDob(new Date(System.currentTimeMillis()));
        user1.setPhone("0123456789");
        user1.setEmail("admin@gmail.com");
        user1.setPassword(passwordEncoder.encode("123456"));
        user1.setVerified(true);
        user1.setBlocked(false);
        user1.setAvatar(file);
        user1.setRole(Role.ROLE_ADMIN);

        User user2_1 = new User();
        user2_1.setName("customer1");
        user2_1.setDob(new Date(System.currentTimeMillis()));
        user2_1.setPhone("0223456789");
        user2_1.setEmail("customer1@gmail.com");
        user2_1.setPassword(passwordEncoder.encode("123456"));
        user2_1.setVerified(true);
        user2_1.setBlocked(false);
        user2_1.setAvatar(file);
        user2_1.setRole(Role.ROLE_CUSTOMER);

        User user2_2 = new User();
        user2_2.setName("customer2");
        user2_2.setDob(new Date(System.currentTimeMillis()));
        user2_2.setPhone("0923456789");
        user2_2.setEmail("customer2@gmail.com");
        user2_2.setPassword(passwordEncoder.encode("123456"));
        user2_2.setVerified(true);
        user2_2.setBlocked(false);
        user2_2.setAvatar(file);
        user2_2.setRole(Role.ROLE_CUSTOMER);

        User user3 = new User();
        user3.setName("staff1");
        user3.setDob(new Date(System.currentTimeMillis()));
        user3.setPhone("0333456789");
        user3.setEmail("staff1@gmail.com");
        user3.setPassword(passwordEncoder.encode("123456"));
        user3.setVerified(true);
        user3.setBlocked(false);
        user3.setAvatar(file);
        user3.setRole(Role.ROLE_STAFF);

        User user3_1 = new User();
        user3_1.setName("staff2");
        user3_1.setDob(new Date(System.currentTimeMillis()));
        user3_1.setPhone("0444456789");
        user3_1.setEmail("staff2@gmail.com");
        user3_1.setPassword(passwordEncoder.encode("123456"));
        user3_1.setVerified(true);
        user3_1.setBlocked(false);
        user3_1.setAvatar(file);
        user3_1.setRole(Role.ROLE_STAFF);

        User user3_2 = new User();
        user3_2.setName("staff3");
        user3_2.setDob(new Date(System.currentTimeMillis()));
        user3_2.setPhone("055556789");
        user3_2.setEmail("staff3@gmail.com");
        user3_2.setPassword(passwordEncoder.encode("123456"));
        user3_2.setVerified(true);
        user3_2.setBlocked(false);
        user3_2.setAvatar(file);
        user3_2.setRole(Role.ROLE_STAFF);

        User user3_3 = new User();
        user3_3.setName("staff4");
        user3_3.setDob(new Date(System.currentTimeMillis()));
        user3_3.setPhone("0666666789");
        user3_3.setEmail("staff4@gmail.com");
        user3_3.setPassword(passwordEncoder.encode("123456"));
        user3_3.setVerified(true);
        user3_3.setBlocked(false);
        user3_3.setAvatar(file);
        user3_3.setRole(Role.ROLE_STAFF);

        userRepository.saveAll(List.of(user1, user2_1, user2_2, user3_1, user3_2, user3_3));
    }
    public void seedDefaultTypesServicesCombos(){
        User admin = userRepository.findByEmail("admin@gmail.com");

        File file2 = new File();
        file2.setName("Haircut 1");
        file2.setUrl("https://i.ibb.co/Dw3LQDz/1731309331477.webp");
        file2.setThumbUrl("https://i.ibb.co/tYy3DMQ/1731309331477.webp");
        file2.setMediumUrl("https://i.ibb.co/MPq97DZ/1731309331477.webp");
        file2.setDeleteUrl("https://ibb.co/tYy3DMQ/b835da78842da53b7ff337fab87e053b");
        file2.setOwner(admin);
        file2 = fileRepository.save(file2);

        File file3 = new File();
        file3.setName("Haircut 2");
        file3.setUrl("https://i.ibb.co/j6t771j/1731309384864.webp");
        file3.setThumbUrl("https://i.ibb.co/yf2LLbx/1731309384864.webp");
        file3.setMediumUrl("");
        file3.setDeleteUrl("https://ibb.co/yf2LLbx/9d57fdcfde72279e0a077667ea6b63b9");
        file3.setOwner(admin);
        file3 = fileRepository.save(file3);


        File file4 = new File();
        file4.setName("Shave 1");
        file4.setUrl("https://i.ibb.co/d54RD0x/1731309566871.jpg");
        file4.setThumbUrl("https://i.ibb.co/5RhPLWw/1731309566871.jpg");
        file4.setMediumUrl("https://i.ibb.co/fxdsHYL/1731309566871.jpg");
        file4.setDeleteUrl("https://ibb.co/5RhPLWw/3f1156c3688358f2440d84b140afa2ec");
        file4.setOwner(admin);
        file4 = fileRepository.save(file4);

        File file5 = new File();
        file5.setName("Shave 2");
        file5.setUrl("https://i.ibb.co/M7s8R4X/1731309600420.webp");
        file5.setThumbUrl("https://i.ibb.co/hVyXKjq/1731309600420.webp");
        file5.setMediumUrl("https://i.ibb.co/FzKsBMP/1731309600420.webp");
        file5.setDeleteUrl("https://ibb.co/hVyXKjq/d72a1064595d3b5d2851ea6999acadb7");
        file5.setOwner(admin);
        file5 = fileRepository.save(file5);

        File file6 = new File();
        file6.setName("Beard Styling 1");
        file6.setUrl("https://i.ibb.co/c8JJty0/1731309661972.jpg");
        file6.setThumbUrl("https://i.ibb.co/VCVVDLR/1731309661972.jpg");
        file6.setMediumUrl("");
        file6.setDeleteUrl("https://ibb.co/VCVVDLR/67e47a62cb5ca23c0f996a0bf0bbe94f");
        file6.setOwner(admin);
        file6 = fileRepository.save(file6);

        File file7 = new File();
        file7.setName("Beard Styling 2");
        file7.setUrl("https://i.ibb.co/bJgrRgg/1731309679703.png");
        file7.setThumbUrl("https://i.ibb.co/0tjfVjj/1731309679703.png");
        file7.setMediumUrl("https://i.ibb.co/xHSM8SS/1731309679703.png");
        file7.setDeleteUrl("https://ibb.co/0tjfVjj/6b6bb0a9b44362c2a4078dcb47f19baa");
        file7.setOwner(admin);
        file7 = fileRepository.save(file7);

        File file8 = new File();
        file8.setName("Hair Coloring 1");
        file8.setUrl("https://i.ibb.co/qJyHbm5/1731309729210.jpg");
        file8.setThumbUrl("https://i.ibb.co/vXwN2LY/1731309729210.jpg");
        file8.setMediumUrl("https://i.ibb.co/Bnzh8TN/1731309729210.jpg");
        file8.setDeleteUrl("https://ibb.co/vXwN2LY/a9636bc8e15288e42c88bd5337cf8cb5");
        file8.setOwner(admin);
        file8 = fileRepository.save(file8);

        File file9 = new File();
        file9.setName("Hair Coloring 2");
        file9.setUrl("https://i.ibb.co/6W1Kb5N/1731309745228.jpg");
        file9.setThumbUrl("https://i.ibb.co/yQYz5JV/1731309745228.jpg");
        file9.setMediumUrl("https://i.ibb.co/fxn3t7Q/1731309745228.jpg");
        file9.setDeleteUrl("https://ibb.co/yQYz5JV/0e456e8225809a7f764c25e7130dc286");
        file9.setOwner(admin);
        file9 = fileRepository.save(file9);

        File file10 = new File();
        file10.setName("Facial 1");
        file10.setUrl("https://i.ibb.co/LrCcL5Y/1731309783967.jpg");
        file10.setThumbUrl("https://i.ibb.co/G9V8KTn/1731309783967.jpg");
        file10.setMediumUrl("https://i.ibb.co/T1H7VLY/1731309783967.jpg");
        file10.setDeleteUrl("https://ibb.co/G9V8KTn/2b0463f391ed875ac1362d06d4b3009d");
        file10.setOwner(admin);
        file10 = fileRepository.save(file10);

        File file11 = new File();
        file11.setName("Facial 2");
        file11.setUrl("https://i.ibb.co/D8gKsS3/1731309816808.jpg");
        file11.setThumbUrl("https://i.ibb.co/F0q7NjG/1731309816808.jpg");
        file11.setMediumUrl("https://i.ibb.co/CMs0NTx/1731309816808.jpg");
        file11.setDeleteUrl("https://ibb.co/F0q7NjG/49a4180ed475991a336c2ae3c4ccb372");
        file11.setOwner(admin);
        file11 = fileRepository.save(file11);

        File file12 = new File();
        file12.setName("Massage 1");
        file12.setUrl("https://i.ibb.co/VYCGCR7/1731309874630.jpg");
        file12.setThumbUrl("https://i.ibb.co/qYg8gKt/1731309874630.jpg");
        file12.setMediumUrl("https://i.ibb.co/B26J6bX/1731309874630.jpg");
        file12.setDeleteUrl("https://ibb.co/qYg8gKt/99621b2d0b2929651e0e87695b389aab");
        file12.setOwner(admin);
        file12 = fileRepository.save(file12);

        File file13 = new File();
        file13.setName("Massage 2");
        file13.setUrl("https://i.ibb.co/3h7n5tz/1731309919982.jpg");
        file13.setThumbUrl("https://i.ibb.co/gg3xbsV/1731309919982.jpg");
        file13.setMediumUrl("https://i.ibb.co/Lp83D45/1731309919982.jpg");
        file13.setDeleteUrl("https://ibb.co/gg3xbsV/aa01599e4d99ba595a272f04288d7e03");
        file13.setOwner(admin);
        file13 = fileRepository.save(file13);

        //done seed images

        ServiceType serviceType1 = new ServiceType();
        serviceType1.setName("Haircut");
        serviceType1 = serviceTypeRepository.save(serviceType1);

        ServiceType serviceType2 = new ServiceType();
        serviceType2.setName("Shave");
        serviceType2 = serviceTypeRepository.save(serviceType2);

        ServiceType serviceType3 = new ServiceType();
        serviceType3.setName("Beard Styling");
        serviceType3 = serviceTypeRepository.save(serviceType3);

        ServiceType serviceType4 = new ServiceType();
        serviceType4.setName("Hair Coloring");
        serviceType4 = serviceTypeRepository.save(serviceType4);

        ServiceType serviceType5 = new ServiceType();
        serviceType5.setName("Facial");
        serviceType5 = serviceTypeRepository.save(serviceType5);

        ServiceType serviceType6 = new ServiceType();
        serviceType6.setName("Massage");
        serviceType6 = serviceTypeRepository.save(serviceType6);
        // done seed types

        Service service1 = new Service();
        service1.setName("Basic Haircut");
        service1.setDescription("A classic haircut for men with minimal styling");
        service1.setPrice(70000);
        service1.setEstimateTime(30);
        service1.setServiceType(serviceType1);
        service1.setImages(List.of(file2));
        service1 = serviceRepository.save(service1);

        Service service2 = new Service();
        service2.setName("Premium Haircut");
        service2.setDescription("A detailed haircut with precision styling and finishing");
        service2.setPrice(100000);
        service2.setEstimateTime(45);
        service2.setServiceType(serviceType1);
        service2.setImages(List.of(file3));
        service2 = serviceRepository.save(service2);

        Service service3 = new Service();
        service3.setName("Beard Trim");
        service3.setDescription("A quick beard trim to maintain shape");
        service3.setPrice(50000);
        service3.setEstimateTime(15);
        service3.setServiceType(serviceType3);
        service3.setImages(List.of(file4));
        service3 = serviceRepository.save(service3);

        Service service4 = new Service();
        service4.setName("Beard Shaping and Styling");
        service4.setDescription("Detailed beard shaping, trimming, and styling for a sharp look");
        service4.setPrice(70000);
        service4.setEstimateTime(30);
        service4.setServiceType(serviceType3);
        service4.setImages(List.of(file5));
        service4 = serviceRepository.save(service4);

        Service service5 = new Service();
        service5.setName("Basic Shave");
        service5.setDescription("A quick shave with minimal styling");
        service5.setPrice(60000);
        service5.setEstimateTime(20);
        service5.setServiceType(serviceType2);
        service5.setImages(List.of(file6));
        service5 = serviceRepository.save(service5);

        Service service6 = new Service();
        service6.setName("Premium Shave");
        service6.setDescription("A relaxing shave with hot towel and facial treatment");
        service6.setPrice(80000);
        service6.setEstimateTime(30);
        service6.setServiceType(serviceType2);
        service6.setImages(List.of(file7));
        service6 = serviceRepository.save(service6);

        Service service7 = new Service();
        service7.setName("Express Facial");
        service7.setDescription("A quick facial treatment for refreshed skin");
        service7.setPrice(80000);
        service7.setEstimateTime(20);
        service7.setServiceType(serviceType5);
        service7.setImages(List.of(file8));
        service7 = serviceRepository.save(service7);

        Service service8 = new Service();
        service8.setName("Deluxe Facial");
        service8.setDescription("Deep cleansing and revitalizing facial for a glowing complexion");
        service8.setPrice(100000);
        service8.setEstimateTime(40);
        service8.setServiceType(serviceType5);
        service8.setImages(List.of(file9));
        service8 = serviceRepository.save(service8);

        Service service9 = new Service();
        service9.setName("Head Massage");
        service9.setDescription("A relaxing massage to relieve head and neck tension");
        service9.setPrice(120000);
        service9.setEstimateTime(15);
        service9.setServiceType(serviceType6);
        service9.setImages(List.of(file10));
        service9 = serviceRepository.save(service9);

        Service service10 = new Service();
        service10.setName("Full Body Massage");
        service10.setDescription("Full body relaxation with stress-relieving techniques");
        service10.setPrice(300000);
        service10.setEstimateTime(60);
        service10.setServiceType(serviceType6);
        service10.setImages(List.of(file11));
        service10 = serviceRepository.save(service10);

        Service service11 = new Service();
        service11.setName("Basic hair dye");
        service11.setDescription("Basic hair dye");
        service11.setPrice(500000);
        service11.setEstimateTime(120);
        service11.setServiceType(serviceType4);
        service11.setImages(List.of(file12));
        service11 = serviceRepository.save(service11);

        Service service12 = new Service();
        service12.setName("Premium hair dye");
        service12.setDescription("Premium hair dye");
        service12.setPrice(1000000);
        service12.setEstimateTime(180);
        service12.setServiceType(serviceType4);
        service12.setImages(List.of(file13));
        service12 = serviceRepository.save(service12);

        // done seed service

        Combo combo1 = new Combo();
        combo1.setName("The Executive Combo");
        combo1.setDescription("A complete grooming package for a polished, executive look");
        combo1.setServices(List.of(service2, service6, service8));
        combo1.setPrice(250000);
        combo1.setEstimateTime(105);
        combo1.setImages(List.of(file3, file7, file9));
        comboRepository.save(combo1);

        Combo combo2 = new Combo();
        combo2.setName("The Gentleman’s Combo");
        combo2.setDescription("Essential grooming for a fresh, refined look");
        combo2.setServices(List.of(service1, service3, service5));
        combo2.setPrice(180000);
        combo2.setEstimateTime(65);
        combo2.setImages(List.of(file3, file7, file9));
        comboRepository.save(combo2);

        Combo combo3 = new Combo();
        combo3.setName("Relax and Revive Combo");
        combo3.setDescription("Rejuvenating facial and head massage for relaxation and stress relief");
        combo3.setServices(List.of(service8, service9));
        combo3.setPrice(200000);
        combo3.setEstimateTime(55);
        combo3.setImages(List.of(file9, file10));
        comboRepository.save(combo3);

        Combo combo4 = new Combo();
        combo4.setName("Premium Grooming Combo");
        combo4.setDescription("A top-notch grooming package with all the premium treatments");
        combo4.setServices(List.of(service2, service4, service7));
        combo4.setPrice(220000);
        combo4.setEstimateTime(95);
        combo4.setImages(List.of(file3, file5, file8));
        comboRepository.save(combo4);
    }
}

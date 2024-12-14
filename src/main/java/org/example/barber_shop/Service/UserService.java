package org.example.barber_shop.Service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Config.SecurityUser;
import org.example.barber_shop.Entity.*;
import org.example.barber_shop.Exception.*;
import org.example.barber_shop.Repository.*;
import org.example.barber_shop.Util.SecurityUtils;
import org.example.barber_shop.Constants.Role;
import org.example.barber_shop.DTO.File.FileResponse;
import org.example.barber_shop.DTO.User.*;
import org.example.barber_shop.Mapper.FileMapper;
import org.example.barber_shop.Mapper.UserMapper;
import org.example.barber_shop.Util.JWTUtil;
import org.example.barber_shop.Util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final FileUploadService fileUploadService;
    private final FileRepository fileRepository;
    private final FileMapper fileMapper;
    private final LoggedOutTokenRepository loggedOutTokenRepository;
    private final StaffShiftRepository staffShiftRepository;
    private final StaffSalaryRepository staffSalaryRepository;
    private final BookingRepository bookingRepository;

    @Value("${token_ttl}")
    private int tokenTtl;

    public long getTokenTTL(){
        Instant now = Instant.now();
        Instant futureTime = now.plus(tokenTtl, ChronoUnit.MILLIS);
        return futureTime.toEpochMilli();
    }

    public UserResponse register(RegisterRequest registerRequest) {
        if (Validator.isValidEmail(registerRequest.email)){
            if (Validator.isValidPhone(registerRequest.phone)){
                if (Validator.isValidPassword(registerRequest.password)){
                    User user = userRepository.findByEmailOrPhone(registerRequest.email, registerRequest.phone);
                    if (user == null) {
                        if (registerRequest.password.equals(registerRequest.re_password)){
                            User user1 = userMapper.toEntity(registerRequest);
                            user1.setRole(Role.ROLE_CUSTOMER);
                            user1.setAvatar(fileRepository.findByName("default-avatar"));
                            user1.setPassword(passwordEncoder.encode(registerRequest.password));
                            String token = UUID.randomUUID() + "-" + getTokenTTL();
                            user1.setBlocked(false);
                            user1.setVerified(false);
                            user1.setToken(token);
                            mailService.sendMailToken(user1.getEmail(), "Verify your email address", token);
                            User savedUser = userRepository.save(user1);
                            return userMapper.toResponse(savedUser);
                        } else {
                            throw new LocalizedException("passwords.not.match");
                        }
                    } else {
                        if (user.getPhone().equals(registerRequest.phone)) {
                            throw new LocalizedException("phone.in.use");
                        } else {
                            throw new LocalizedException("email.in.use");
                        }
                    }
                } else {
                    throw new LocalizedException("invalid.password.format");
                }
            } else {
                throw new LocalizedException("invalid.phone.format");
            }

        } else {
            throw new LocalizedException("invalid.email.format");
        }

    }
    public static long extractTTLFromToken(String token) {
        String[] parts = token.split("-");
        if (parts.length == 6) {
            return Long.parseLong(parts[5]);
        } else {
            throw new LocalizedException("invalid.token");
        }
    }
    public static boolean isTokenValid(long ttl) {
        long currentTime = System.currentTimeMillis();
        return currentTime < ttl;
    }
    public boolean verifyToken(String token) {
        long ttl = extractTTLFromToken(token);
        boolean isValid = isTokenValid(ttl);
        if (isValid) {
            User user = userRepository.findByToken(token);
            if (user != null){
                user.setVerified(true);
                user.setToken(null);
                userRepository.save(user);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    public String login(LoginRequest loginRequest){
        User user = userRepository.findByEmail(loginRequest.email);
        if (user != null){
            try {
                Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password));
                SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
                user = securityUser.getUser();
                return jwtUtil.generateToken(user);
            } catch (Exception e){
                e.printStackTrace();
                throw new LocalizedException("invalid.credentials");
            }
        } else {
            throw new LocalizedException("invalid.credentials");
        }
    }
    public List<UserResponse> getAllUsers(){
        return userMapper.toResponses(userRepository.findAll());
    }
    public UserResponse getProfile(){
        long id = SecurityUtils.getCurrentUserId();
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()){
            throw new LocalizedException("user.not.found");
        } else {
            return userMapper.toResponse(user.get());
        }
    }
    public UserResponse updateProfile(UpdateProfileRequest updateProfileRequest){
        User user = userRepository.findById(SecurityUtils.getCurrentUserId()).orElse(null);
        if (user != null){
            if (user.getRole() == Role.ROLE_STAFF){
                user.setDescription(updateProfileRequest.description);
            }
            user.setName(updateProfileRequest.name);
            user.setPhone(updateProfileRequest.phone);
            user.setDob(updateProfileRequest.dob);
            User savedUser = userRepository.save(user);
            return userMapper.toResponse(savedUser);
        } else {
            throw new LocalizedException("user.not.found");
        }
    }
    public FileResponse updateAvatar(MultipartFile file) throws IOException {
        JsonNode jsonNode = fileUploadService.uploadToImgBB(file);
        File file1 = new File();
        file1.setName(jsonNode.path("data").path("image").path("name").asText());
        file1.setUrl(jsonNode.path("data").path("url").asText());
        file1.setThumbUrl(jsonNode.path("data").path("thumb").path("url").asText());
        file1.setMediumUrl(jsonNode.path("data").path("medium").path("url").asText());
        file1.setDeleteUrl(jsonNode.path("data").path("delete_url").asText());
        User user = userRepository.findById(SecurityUtils.getCurrentUserId()).get();
        file1.setOwner(user);
        File savedFile = fileRepository.save(file1);
        user.setAvatar(savedFile);
        userRepository.save(user);
        return fileMapper.toFileResponse(savedFile);
    }
    public List<StaffResponse> getAllStaffs(){
        try {
            List<User> staffs = userRepository.findAllByRole(Role.ROLE_STAFF);
            List<Booking> bookings = bookingRepository.findByStaffIn(staffs);
            List<StaffResponse> staffResponses = userMapper.toStaffResponses(staffs);
            for (int i = 0; i < staffs.size(); i++) {
                if (bookings.isEmpty()){
                    staffResponses.get(i).rating = 0;
                    staffResponses.get(i).bookingCount = 0;
                } else {
                    int sumRating = 0;
                    int bookingCount = 0;
                    for (int j = 0; j < bookings.size(); j++) {
                        if (Objects.equals(staffs.get(i).getId(), bookings.get(j).getStaff().getId())) {
                            if (bookings.get(j).getReview() != null){
                                sumRating += bookings.get(j).getReview().getStaffRating();
                            }
                            bookingCount++;
                        }
                    }
                    if (bookingCount == 0){
                        staffResponses.get(i).rating = 0;
                    } else {
                        staffResponses.get(i).rating = (float) sumRating /bookingCount;
                    }
                    staffResponses.get(i).bookingCount = bookingCount;
                }
            }
            return staffResponses;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public List<UserResponse> getAllCustomers(){
        return userMapper.toResponses(userRepository.findAllByRole(Role.ROLE_CUSTOMER));
    }
    public List<UserResponse> getAllAdmins(){
        return userMapper.toResponses(userRepository.findAllByRole(Role.ROLE_ADMIN));
    }
    public boolean logout(String authHeader){
        String token = authHeader.substring(7);
        LoggedOutToken loggedOutToken = new LoggedOutToken();
        loggedOutToken.setUser(SecurityUtils.getCurrentUser());
        loggedOutToken.setToken(token);
        Object object = jwtUtil.getValueFromJwt(token, "exp");
        if (object instanceof Integer expTime){
            loggedOutToken.setExpTime(new Timestamp(expTime));
            loggedOutTokenRepository.save(loggedOutToken);
            return true;
        } else {
            return false;
        }
    }
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest){
        User user = userRepository.findByEmail(forgotPasswordRequest.email);
        if (user == null){
            return;
        }
        String token = UUID.randomUUID() + "-" + getTokenTTL();
        user.setToken(token);
        userRepository.save(user);
        mailService.sendMailToken(forgotPasswordRequest.email, "Reset your password", token);
    }
    public void resetPassword(ResetPasswordRequest resetPasswordRequest){
        long ttl = extractTTLFromToken(resetPasswordRequest.token);
        if (isTokenValid(ttl)){
            User user = userRepository.findByToken(resetPasswordRequest.token);
            if (user == null){
                throw new LocalizedException("invalid.token");
            } else {
                if (resetPasswordRequest.password.equals(resetPasswordRequest.re_password)){
                    user.setPassword(passwordEncoder.encode(resetPasswordRequest.password));
                    user.setToken(null);
                    userRepository.save(user);
                } else {
                    throw new LocalizedException("passwords.not.match");
                }
            }
        } else {
            throw new LocalizedException("invalid.token");
        }
    }
    public UserResponse updateUser(UpdateUserRequest updateUserRequest){
        Optional<User> userOptional = userRepository.findById(updateUserRequest.id);
        if (userOptional.isPresent()){
            User checkMail = userRepository.findByEmail(updateUserRequest.email);
            if (checkMail != null && checkMail.getId() != updateUserRequest.id){
                throw new LocalizedException("email.in.use");
            }
            User checkPhone = userRepository.findByPhone(updateUserRequest.phone);
            if (checkPhone != null && checkPhone.getId() != updateUserRequest.id){
                throw new LocalizedException("phone.in.use");
            }
            User user = userOptional.get();
            user.setName(updateUserRequest.name);
            user.setPhone(updateUserRequest.phone);
            user.setDob(updateUserRequest.dob);
            user.setEmail(updateUserRequest.email);
            if (user.getRole() == Role.ROLE_STAFF){
                user.setDescription(updateUserRequest.description);
            }
            if (updateUserRequest.password != null){
                if (!updateUserRequest.password.isEmpty()){
                    user.setPassword(passwordEncoder.encode(updateUserRequest.password));
                }
            }
            user.setBlocked(updateUserRequest.blocked);
            user = userRepository.save(user);
            return userMapper.toResponse(user);
        } else {
            throw new LocalizedException("user.not.found");
        }
    }
    public UserResponse adminCreateUser(AdminCreateUser adminCreateUser){
        if (Validator.isValidEmail(adminCreateUser.email)){
            if (Validator.isValidPhone(adminCreateUser.phone)){
                if (Validator.isValidPassword(adminCreateUser.password)){
                    if (userRepository.findByEmail(adminCreateUser.email) == null){
                        if (userRepository.findByPhone(adminCreateUser.phone) == null){
                            User user = new User();
                            user.setName(adminCreateUser.name);
                            user.setEmail(adminCreateUser.email);
                            user.setPhone(adminCreateUser.phone);
                            user.setDob(adminCreateUser.dob);
                            user.setPassword(passwordEncoder.encode(adminCreateUser.password));
                            user.setRole(adminCreateUser.role);
                            user.setBlocked(false);
                            user.setVerified(true);
                            File file = fileRepository.findByName("default-avatar");
                            user.setAvatar(file);
                            user = userRepository.save(user);
                            if (adminCreateUser.role == Role.ROLE_STAFF){
                                if (Validator.isOver18YearsOld(adminCreateUser.dob)){
                                    user.setDescription(adminCreateUser.description);
                                    StaffSalary staffSalary = new StaffSalary();
                                    staffSalary.setStaff(user);
                                    staffSalary.setRate(25000);
                                    staffSalary.setPercentage(10);
                                    staffSalaryRepository.save(staffSalary);
                                } else {
                                    throw new LocalizedException("staff.age.restriction");
                                }
                            }
                            return userMapper.toResponse(userRepository.save(user));
                        } else {
                            throw new LocalizedException("phone.in.use");
                        }
                    } else {
                        throw new LocalizedException("email.in.use");
                    }
                } else {
                    throw new LocalizedException("invalid.password.format");
                }
            } else {
                throw new LocalizedException("invalid.phone.format");
            }
        } else {
            throw new LocalizedException("invalid.email.format");
        }
    }
    public UserResponseNoFile blockUser(long id){
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()){
            User user = userOptional.get();
            user.setBlocked(true);
            return userMapper.toResponseNoFile(userRepository.save(user));
        } else {
            throw new LocalizedException("user.not.found");
        }
    }
}

package org.example.barber_shop.Service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Config.SecurityUser;
import org.example.barber_shop.Util.SecurityUtils;
import org.example.barber_shop.Constants.Role;
import org.example.barber_shop.DTO.File.FileResponse;
import org.example.barber_shop.DTO.User.*;
import org.example.barber_shop.Entity.File;
import org.example.barber_shop.Entity.LoggedOutToken;
import org.example.barber_shop.Entity.User;
import org.example.barber_shop.Exception.EmailExistException;
import org.example.barber_shop.Exception.PasswordMismatchException;
import org.example.barber_shop.Exception.PhoneExistException;
import org.example.barber_shop.Mapper.FileMapper;
import org.example.barber_shop.Mapper.UserMapper;
import org.example.barber_shop.Repository.FileRepository;
import org.example.barber_shop.Repository.LoggedOutTokenRepository;
import org.example.barber_shop.Repository.UserRepository;
import org.example.barber_shop.Util.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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

    @Value("${token_ttl}")
    private int tokenTtl;

    public long getTokenTTL(){
        Instant now = Instant.now();
        Instant futureTime = now.plus(tokenTtl, ChronoUnit.MILLIS);
        return futureTime.toEpochMilli();
    }

    public UserResponse register(RegisterRequest registerRequest) {
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
                throw new PasswordMismatchException("Passwords do not match.");
            }
        } else {
            if (user.getPhone().equals(registerRequest.phone)) {
                throw new PhoneExistException("This phone number is already in use.");
            } else {
                throw new EmailExistException("This email address is already in use.");
            }
        }
    }
    public static long extractTTLFromToken(String token) {
        String[] parts = token.split("-");
        if (parts.length == 6) {
            return Long.parseLong(parts[5]);
        } else {
            throw new IllegalArgumentException("Invalid token format.");
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
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password));
            SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
            user = securityUser.getUser();
            return jwtUtil.generateToken(user);
        } else {
            throw new BadCredentialsException("Bad credentials");
        }

    }
    public List<UserResponse> getAllUsers(){
        return userMapper.toResponses(userRepository.findAll());
    }
    public UserResponse getProfile(){
        long id = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(id).get();
        return userMapper.toResponse(user);
    }
    public UserResponse updateProfile(UpdateProfileRequest updateProfileRequest){
        User user = userRepository.findById(SecurityUtils.getCurrentUserId()).get();
        user.setName(updateProfileRequest.name);
        user.setPhone(updateProfileRequest.phone);
        user.setDob(updateProfileRequest.dob);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
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
    public List<UserResponse> getAllStaffs(){
        return userMapper.toResponses(userRepository.findAllByRole(Role.ROLE_STAFF));
    }
    public List<UserResponse> getAllCustomers(){
        return userMapper.toResponses(userRepository.findAllByRole(Role.ROLE_CUSTOMER));
    }
    public List<UserResponse> getAllAdmins(){
        return userMapper.toResponses(userRepository.findAllByRole(Role.ROLE_ADMIN));
    }
    public List<UserResponse> getAllReceptionists(){
        return userMapper.toResponses(userRepository.findAllByRole(Role.ROLE_RECEPTIONIST));
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
        if (user != null){
            String token = UUID.randomUUID() + "-" + getTokenTTL();
            user.setToken(token);
            userRepository.save(user);
            mailService.sendMailToken(forgotPasswordRequest.email, "Reset your password", token);
        }
    }
    public void resetPassword(ResetPasswordRequest resetPasswordRequest){
        long ttl = extractTTLFromToken(resetPasswordRequest.token);
        if (isTokenValid(ttl)){
            User user = userRepository.findByToken(resetPasswordRequest.token);
            if (user == null){
                throw new RuntimeException("Invalid token.");
            } else {
                if (resetPasswordRequest.password.equals(resetPasswordRequest.re_password)){
                    user.setPassword(passwordEncoder.encode(resetPasswordRequest.password));
                    user.setToken(null);
                    userRepository.save(user);
                } else {
                    throw new PasswordMismatchException("Passwords do not match.");
                }
            }
        } else {
            throw new RuntimeException("Invalid token.");
        }
    }
}

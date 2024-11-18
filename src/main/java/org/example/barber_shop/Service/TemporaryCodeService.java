package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Entity.User;
import org.example.barber_shop.Repository.UserRepository;
import org.example.barber_shop.Util.JWTUtil;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class TemporaryCodeService {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final Map<String, String> codeStore = new ConcurrentHashMap<>();
    private final long CODE_EXPIRATION_TIME = 5 * 60 * 1000;

    public String generateCode(String userId) {
        String code = UUID.randomUUID().toString();
        codeStore.put(code, userId);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                codeStore.remove(code);
            }
        }, CODE_EXPIRATION_TIME);

        return code;
    }
    public String getUserId(String code) {
        return codeStore.remove(code);
    }
    public String handleExchangeRequest(Map<String, String> request){
        String code = request.get("code");
        String userId = getUserId(code);
        if (userId == null) {
            throw new RuntimeException("INVALID_CODE");
        } else {
            User user = userRepository.findById(Long.parseLong(userId)).orElse(null);
            if (user == null) {
                throw new RuntimeException("INVALID_CODE");
            } else {
                return jwtUtil.generateToken(user);
            }
        }
    }
}

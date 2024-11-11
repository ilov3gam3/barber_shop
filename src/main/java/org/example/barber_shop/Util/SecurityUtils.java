package org.example.barber_shop.Util;

import org.example.barber_shop.Config.SecurityUser;
import org.example.barber_shop.Entity.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SecurityUtils {
    public static Long getCurrentUserId(){
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principle instanceof UserDetails){
            return ((SecurityUser) principle).getUser().getId();
        }
        return 0L;
    }
    public static User getCurrentUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((SecurityUser) principal).getUser();
        }
        return new User();
    }
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return !(authentication instanceof AnonymousAuthenticationToken);
    }

    public static boolean isCurrentUser(Long userId) {

        SecurityUser principal = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return Objects.equals(principal.getUser().getId(), userId);
    }
}

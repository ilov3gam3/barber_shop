package org.example.barber_shop.Util;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

public class Validator {
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return Pattern.matches(emailRegex, email);
    }

    public static boolean isValidPhone(String phone) {
        String phoneRegex = "^(03|05|07|08|09)\\d{8}$";
        return Pattern.matches(phoneRegex, phone);
    }

    public static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z]).{8,}$";
        return Pattern.matches(passwordRegex, password);
    }
    public static boolean isOver18YearsOld(Date dob) {
        if (dob == null) {
            return false;
        }
        LocalDate dobLocalDate = dob.toLocalDate();
        LocalDate today = LocalDate.now();
        Period age = Period.between(dobLocalDate, today);
        return age.getYears() >= 18;
    }
}

package org.example.barber_shop.Constants;

public enum Role {
    ROLE_ADMIN,
    ROLE_CUSTOMER,
    ROLE_RECEPTIONIST,
    ROLE_STAFF;
    @Override
    public String toString() {
        return name();
    }
    public static Role getRole(String role){
        if (role == null) {
            return null;
        }
        return switch (role.toUpperCase()){
            case "ADMIN", "ROLE_ADMIN" -> ROLE_ADMIN;
            case "CUSTOMER", "ROLE_CUSTOMER" -> ROLE_CUSTOMER;
            case "RECEPTIONIST", "ROLE_RECEPTIONIST" -> ROLE_RECEPTIONIST;
            case "STAFF", "ROLE_STAFF" -> ROLE_STAFF;
            default -> throw new IllegalArgumentException("No enum constant for role: " + role);
        };
    }
}

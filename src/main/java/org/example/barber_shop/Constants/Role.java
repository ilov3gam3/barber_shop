package org.example.barber_shop.Constants;

public enum Role {
    ROLE_ADMIN,
    ROLE_CUSTOMER,
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
            case "STAFF", "ROLE_STAFF" -> ROLE_STAFF;
            default -> throw new IllegalArgumentException("No enum constant for role: " + role);
        };
    }
}

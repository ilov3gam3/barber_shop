package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.Payment.PaymentResponse;
import org.example.barber_shop.DTO.Payment.PaymentResponseNoUser;
import org.example.barber_shop.DTO.User.UserResponseNoFile;
import org.example.barber_shop.Entity.Payment;
import org.example.barber_shop.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    @Mapping(target = "user", expression = "java(mapFirstCustomer(payment))")
    PaymentResponse toPaymentResponse(Payment payment);

    default UserResponseNoFile mapFirstCustomer(Payment payment) {
        return (payment.getBookings() != null && !payment.getBookings().isEmpty())
                ? toResponseNoFile(payment.getBookings().get(0).getCustomer())
                : null;
    }
    PaymentResponseNoUser toPaymentResponseNoUser(Payment payment);

    List<PaymentResponse> toPaymentResponseList(List<Payment> payments);
    List<PaymentResponseNoUser> toPaymentResponseNoUserList(List<Payment> payments);

    UserResponseNoFile toResponseNoFile(User user);
}

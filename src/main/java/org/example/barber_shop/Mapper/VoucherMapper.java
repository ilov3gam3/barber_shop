package org.example.barber_shop.Mapper;

import org.example.barber_shop.DTO.Voucher.VoucherAddRequest;
import org.example.barber_shop.DTO.Voucher.VoucherResponse;
import org.example.barber_shop.DTO.Voucher.VoucherUpdateRequest;
import org.example.barber_shop.Entity.Voucher;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VoucherMapper {
    VoucherResponse toResponse(Voucher voucher);
    Voucher toEntity(VoucherAddRequest voucherResponse);
    Voucher toEntity(VoucherUpdateRequest voucherUpdateRequest);
    List<VoucherResponse> toResponses(List<Voucher> vouchers);
}

package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.Voucher.VoucherAddRequest;
import org.example.barber_shop.DTO.Voucher.VoucherUpdateRequest;
import org.example.barber_shop.Service.VoucherService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/voucher")
@RequiredArgsConstructor
public class VoucherController {
    private final VoucherService voucherService;
    @PostMapping("")
    public ApiResponse<?> addVoucher(@RequestBody VoucherAddRequest voucherAddRequest){
        return new ApiResponse<>(
                HttpStatus.CREATED.value(), "VOUCHER ADDED", voucherService.addVoucher(voucherAddRequest)
        );
    }
    @PutMapping("")
    public ApiResponse<?> updateVoucher(@RequestBody VoucherUpdateRequest voucherUpdateRequest){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "VOUCHER UPDATED", voucherService.updateVoucher(voucherUpdateRequest)
        );
    }
    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteVoucher(@PathVariable long id){
        if (voucherService.deleteVoucher(id)){
            return new ApiResponse<>(
                    HttpStatus.OK.value(), "VOUCHER DELETED", null
            );
        } else {
            return new ApiResponse<>(
                    HttpStatus.NOT_FOUND.value(), "VOUCHER DELETE FAIL", null
            );
        }
    }
    @GetMapping("")
    public ApiResponse<?> getVouchers(){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "VOUCHER LIST", voucherService.getAllVouchers()
        );
    }
    @GetMapping("/{id}")
    public ApiResponse<?> getVouchers(@PathVariable long id){
        return new ApiResponse<>(
                HttpStatus.OK.value(), "VOUCHER LIST", voucherService.get1Vouchers(id)
        );
    }
}

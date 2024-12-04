package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.Voucher.VoucherAddRequest;
import org.example.barber_shop.DTO.Voucher.VoucherResponse;
import org.example.barber_shop.DTO.Voucher.VoucherUpdateRequest;
import org.example.barber_shop.Entity.Voucher;
import org.example.barber_shop.Mapper.VoucherMapper;
import org.example.barber_shop.Repository.VoucherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    public VoucherResponse addVoucher(VoucherAddRequest voucherAddRequest){
        Voucher voucher = voucherMapper.toEntity(voucherAddRequest);
        voucher = voucherRepository.save(voucher);
        return voucherMapper.toResponse(voucher);
    }
    public VoucherResponse updateVoucher(VoucherUpdateRequest voucherUpdateRequest){
        Voucher voucher = voucherMapper.toEntity(voucherUpdateRequest);
        return voucherMapper.toResponse(voucherRepository.save(voucher));
    }
    public List<VoucherResponse> getAllVouchers(){
        return voucherMapper.toResponses(voucherRepository.findByDeletedFalse());
    }
    public boolean deleteVoucher(long id){
        Voucher voucher = voucherRepository.findByIdAndDeletedFalse(id);
        if(voucher != null){
            voucher.setDeleted(true);
            voucherRepository.save(voucher);
            return true;
        } else {
            throw new RuntimeException("Voucher not exist with id " + id);
        }
    }
    public VoucherResponse get1Vouchers(long id){
        Voucher voucher = voucherRepository.findById(id).orElse(null);
        if(voucher != null){
            return voucherMapper.toResponse(voucher);
        } else {
            throw new RuntimeException("Voucher not exist with id " + id);
        }
    }
}

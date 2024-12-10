package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.NotificationType;
import org.example.barber_shop.Constants.Rank;
import org.example.barber_shop.Constants.Role;
import org.example.barber_shop.DTO.Voucher.VoucherAddRequest;
import org.example.barber_shop.DTO.Voucher.VoucherResponse;
import org.example.barber_shop.DTO.Voucher.VoucherUpdateRequest;
import org.example.barber_shop.Entity.Notification;
import org.example.barber_shop.Entity.User;
import org.example.barber_shop.Entity.Voucher;
import org.example.barber_shop.Exception.LocalizedException;
import org.example.barber_shop.Mapper.VoucherMapper;
import org.example.barber_shop.Repository.NotificationRepository;
import org.example.barber_shop.Repository.UserRepository;
import org.example.barber_shop.Repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    @Value("${front_end_server}")
    private String fe_server;
    public VoucherResponse addVoucher(VoucherAddRequest voucherAddRequest){
        Voucher check = voucherRepository.findByCodeAndDeletedFalse(voucherAddRequest.code);
        if(check != null){
            throw new LocalizedException("voucher.code.exist");
        }
        Voucher voucher = voucherMapper.toEntity(voucherAddRequest);
        if (voucher.getForRank() == null){
            voucher.setForRank(Rank.BRONZE);
        }
        voucher = voucherRepository.save(voucher);
        List<User> customers = userRepository.findAllByRole(Role.ROLE_CUSTOMER);
        List<Notification> notifications = new ArrayList<>();
        for (User customer : customers) {
            Notification tempNotification = new Notification();
            tempNotification.setUser(customer);
            tempNotification.setType(NotificationType.NEW_VOUCHER);
            tempNotification.setTitle("New voucher");
            tempNotification.setMessage("New voucher is available now, use it and enjoy our service");
            tempNotification.setTargetUrl(fe_server + "/voucher/" + voucher.getId());
            tempNotification.setSeen(false);
            notifications.add(tempNotification);
        }
        notificationRepository.saveAll(notifications);
        for (Notification notification: notifications){
            User user = notification.getUser();
            notification.setUser(null);
            simpMessagingTemplate.convertAndSendToUser(user.getEmail(), "/topic", notification);
        }
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
            throw new LocalizedException("voucher.not.exist");
        }
    }
    public VoucherResponse get1Vouchers(long id){
        Voucher voucher = voucherRepository.findById(id).orElse(null);
        if(voucher != null){
            return voucherMapper.toResponse(voucher);
        } else {
            throw new LocalizedException("voucher.not.exist");
        }
    }
    public List<VoucherResponse> getAvailableVouchers(){
        List<Voucher> vouchers = voucherRepository.findByDeletedFalseAndDisabledFalse();
        return voucherMapper.toResponses(vouchers);
    }
}

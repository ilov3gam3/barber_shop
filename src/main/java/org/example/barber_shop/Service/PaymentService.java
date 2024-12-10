package org.example.barber_shop.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.Constants.NotificationType;
import org.example.barber_shop.Constants.Role;
import org.example.barber_shop.Constants.TransactionStatus;
import org.example.barber_shop.DTO.Booking.BookingResponseAdmin;
import org.example.barber_shop.DTO.Payment.PaymentRequest;
import org.example.barber_shop.DTO.Payment.PaymentResponse;
import org.example.barber_shop.Entity.*;
import org.example.barber_shop.Exception.LocalizedException;
import org.example.barber_shop.Mapper.BookingMapper;
import org.example.barber_shop.Mapper.PaymentMapper;
import org.example.barber_shop.Repository.*;
import org.example.barber_shop.Util.SecurityUtils;
import org.example.barber_shop.Util.VNPayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final NotificationRepository notificationRepository;
    private final PaymentMapper paymentMapper;
    private final MailService mailService;
    private final BookingMapper bookingMapper;
    private final UserRepository userRepository;
    @Value("${vnp_TmnCode}")
    private String VNP_TMN_CODE;
    @Value("${vnp_ReturnUrl}")
    private String VNP_RETURN_URL;
    @Value("${secretKey}")
    private String SECRET_KEY;
    @Value("${vnp_PayUrl}")
    private String VNP_PAY_URL;
    @Value("${front_end_server}")
    private String fe_server;
    private final VoucherRepository voucherRepository;
    private final RankService rankService;

    public String getVnpayUrl(PaymentRequest paymentRequest, HttpServletRequest request) throws UnsupportedEncodingException {
        if (paymentRequest.bookingIds != null){
            for (int i = 0; i < paymentRequest.bookingIds.size(); i++) {
                if (paymentRequest.bookingIds.get(i) == null){
                    throw new LocalizedException("booking.ids.not.null");
                }
            }
            Long customer_id = SecurityUtils.getCurrentUserId();
            User customer = userRepository.findById(customer_id).orElse(null);
            if (customer == null){
                throw new LocalizedException("customer.not.found");
            }
            List<Booking> bookings = bookingRepository.findAllByIdInAndCustomer_IdAndStatus(paymentRequest.bookingIds, customer_id, BookingStatus.PENDING);
            if (bookings.isEmpty()){
                throw new LocalizedException("booking.id.invalid");
            }
            List<Booking> bookingsPaid = bookingRepository.findAllByStatus(BookingStatus.PAID);
            for (Booking pendingBooking : bookings) {
                for (Booking paidBooking : bookingsPaid) {
                    if (pendingBooking.getStartTime().before(paidBooking.getEndTime()) && pendingBooking.getEndTime().after(paidBooking.getStartTime())) {
                        throw new LocalizedException("booking.id.conflict" , pendingBooking.getId());
                    }
                }
            }
            long amount = getAmount(bookings);
            long discountAmount = 0;
            Voucher voucher = null;
            if (!paymentRequest.voucherCode.isEmpty()){
                voucher = voucherRepository.findByCodeAndDeletedFalse(paymentRequest.voucherCode);
                if (voucher == null){
                    throw new LocalizedException("voucher.invalid");
                }
                if (!voucher.isValid()){
                    throw new LocalizedException("voucher.expired");
                }
                if (!voucher.canUse()){
                    throw new LocalizedException("voucher.out.of.use");
                }
                if (customer.getRank().ordinal() < voucher.getForRank().ordinal()){
                    throw new LocalizedException("voucher.invalid");
                }
                discountAmount = voucher.calculateDiscount(amount/100L);
                amount -= (discountAmount*100L);
            }
            if (bookings.size() == paymentRequest.bookingIds.size()){
                String vnp_Version = "2.1.0";
                String vnp_Command = "pay";
                String orderType = "other";
                String bankCode = paymentRequest.bankCode;
                String vnp_TxnRef = VNPayUtils.getRandomNumber(8);
                String vnp_IpAddr = VNPayUtils.getIpAddress(request);
                String vnp_TmnCode = VNP_TMN_CODE;
                Map<String, String> vnp_Params = new HashMap<>();
                vnp_Params.put("vnp_Version", vnp_Version);
                vnp_Params.put("vnp_Command", vnp_Command);
                vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
                vnp_Params.put("vnp_Amount", String.valueOf(amount));
                vnp_Params.put("vnp_CurrCode", "VND");
                if (bankCode != null && !bankCode.isEmpty()) {
                    vnp_Params.put("vnp_BankCode", bankCode);
                }
                vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
                String bookingIds = paymentRequest.bookingIds.stream().map(String::valueOf).collect(Collectors.joining(","));
                String vnp_OrderInfo = UUID.randomUUID() + "-" + System.currentTimeMillis() + "|" + bookingIds;
                vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
                vnp_Params.put("vnp_OrderType", orderType);
                String locate = paymentRequest.language;
                if (locate != null && !locate.isEmpty()) {
                    vnp_Params.put("vnp_Locale", locate);
                } else {
                    vnp_Params.put("vnp_Locale", "vn");
                }
                vnp_Params.put("vnp_ReturnUrl", VNP_RETURN_URL);
                vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
                Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                String vnp_CreateDate = formatter.format(cld.getTime());
                vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
                cld.add(Calendar.MINUTE, 15);
                String vnp_ExpireDate = formatter.format(cld.getTime());
                vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
                List fieldNames = new ArrayList(vnp_Params.keySet());
                Collections.sort(fieldNames);
                StringBuilder hashData = new StringBuilder();
                StringBuilder query = new StringBuilder();
                Iterator itr = fieldNames.iterator();
                while (itr.hasNext()) {
                    String fieldName = (String) itr.next();
                    String fieldValue = vnp_Params.get(fieldName);
                    if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                        hashData.append(fieldName);
                        hashData.append('=');
                        hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                        query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                        query.append('=');
                        query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                        if (itr.hasNext()) {
                            query.append('&');
                            hashData.append('&');
                        }
                    }
                }
                String queryUrl = query.toString();
                String vnp_SecureHash = VNPayUtils.hmacSHA512(SECRET_KEY, hashData.toString());
                queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
                Payment payment = new Payment();
                payment.setAmount(amount/100);
                payment.setTxnRef(vnp_TxnRef);
                payment.setOrderInfo(vnp_OrderInfo);
                if (!paymentRequest.voucherCode.isEmpty() && voucher != null){
                    payment.setVoucherCode(voucher.getCode());
                    payment.setVoucherDiscount(voucher.getDiscount());
                    payment.setDiscountAmount(discountAmount);
                }
                paymentRepository.save(payment);
                return VNP_PAY_URL + "?" + queryUrl;
            } else {
                throw new LocalizedException("booking.not.paid");
            }
        } else {
            throw new LocalizedException("booking.ids.not.null");
        }
    }
    public long getAmount(List<Booking> bookings){
        long price = 0;
        for (Booking booking : bookings) {
            long tempPrice = 0;
            List<BookingDetail> bookingDetails = booking.getBookingDetails();
            for (BookingDetail bookingDetail : bookingDetails) {
                if (bookingDetail.getService() != null) {
                    tempPrice += bookingDetail.getService().getPrice();
                }
                if (bookingDetail.getCombo() != null) {
                    tempPrice += bookingDetail.getCombo().getPrice();
                }
            }
            price += tempPrice;
        }
        return price * 100L;
    }
    public String handleVnpayResult(HttpServletRequest httpServletRequest) throws UnsupportedEncodingException {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<?> params = httpServletRequest.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
            String fieldValue = URLEncoder.encode(httpServletRequest.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");
        String signValue = VNPayUtils.hashAllFields(fields, SECRET_KEY);
        if (signValue.equals(httpServletRequest.getParameter("vnp_SecureHash"))){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat sqlFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            long amount = Long.parseLong(httpServletRequest.getParameter("vnp_Amount").replaceFirst("00", ""));
            String paid_at = httpServletRequest.getParameter("vnp_PayDate");
            String vnp_OrderInfo = httpServletRequest.getParameter("vnp_OrderInfo");
            Payment payment = paymentRepository.findByOrderInfo(vnp_OrderInfo);
            if (payment != null) {
                if (payment.transactionStatus == null){
                    String vnp_TransactionStatus = httpServletRequest.getParameter("vnp_TransactionStatus");
                    String vnp_TransactionNo = httpServletRequest.getParameter("vnp_TransactionNo");
                    String vnp_BankTranNo = httpServletRequest.getParameter("vnp_BankTranNo");
                    String vnp_CardType = httpServletRequest.getParameter("vnp_CardType");
                    String vnp_BankCode = httpServletRequest.getParameter("vnp_BankCode");
                    try {
                        paid_at = sqlFormatter.format(formatter.parse(paid_at));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        throw new LocalizedException("err.process.payment");
                    }
                    String booking_ids_string = vnp_OrderInfo.split("\\|")[1];
                    List<Long> booking_ids = Arrays.stream(booking_ids_string.split(",")).map(Long::parseLong).toList();
                    List<Booking> bookings = bookingRepository.findByIdIn(booking_ids);
                    bookings = bookingRepository.saveAll(bookings);
                    for (Booking booking : bookings){
                        bookingDetailRepository.saveAll(booking.getBookingDetails());
                    }
                    payment.setBankCode(vnp_BankCode);
                    payment.setTransactionNo(vnp_TransactionNo);
                    payment.setTransactionStatus(TransactionStatus.fromCode(vnp_TransactionStatus));
                    payment.setCardType(vnp_CardType);
                    payment.setBankTranNo(vnp_BankTranNo);
                    payment.setPaid_at(Timestamp.valueOf(paid_at));
                    payment.setBookings(bookings);
                    payment = paymentRepository.save(payment);
                    if (payment.getVoucherCode() != null){
                        Voucher voucher = voucherRepository.findByCodeAndDeletedFalse(payment.voucherCode);
                        if (voucher == null) {
                            System.out.println("cant find voucher with code " + payment.voucherCode);
                        } else {
                            voucher.setUses(voucher.getUses() + 1);
                            voucherRepository.save(voucher);
                        }
                    }
                    for (Booking booking : bookings){
                        booking.setStatus(BookingStatus.PAID);
                        booking.setPayment(payment);
                        long temp = 0;
                        for (BookingDetail bookingDetail : booking.getBookingDetails()){
                            if (bookingDetail.getService() != null){
                                bookingDetail.setFinalPrice(bookingDetail.getService().getPrice());
                                temp += bookingDetail.getService().getPrice();
                            }
                            if (bookingDetail.getCombo() != null){
                                bookingDetail.setFinalPrice(bookingDetail.getCombo().getPrice());
                                temp += bookingDetail.getCombo().getPrice();
                            }
                        }
                        booking.setTotalPrice(temp);
                    }
                    bookingRepository.saveAll(bookings);
                    Notification notification = new Notification();
                    notification.setUser(payment.getBookings().get(0).getCustomer());
                    notification.setType(NotificationType.PAYMENT_SUCCESS);
                    notification.setTitle("Payment Success");
                    notification.setMessage("Payment Success");
                    notification.setTargetUrl(fe_server + "/temp/payment/" + payment.getId()); // Optionally, link to payment page
                    notification.setSeen(false);
                    notification = notificationRepository.save(notification);
                    notification.setUser(null);
                    simpMessagingTemplate.convertAndSendToUser(payment.getBookings().get(0).getCustomer().getEmail(), "/topic", notification);
                    mailService.sendMailPaymentSuccess(payment.getBookings().get(0).getCustomer().getEmail(), notification.getTargetUrl());
                    rankService.checkRank(bookings.get(0).getCustomer().getId());
                    return "success";
                }
                else {
                    System.out.println("payment đã được thanh toán.");
                    return "fail";
                }
            } else {
                System.out.println("payment không tồn tại.");
                return "fail";
            }
        } else {
            System.out.println("chữ kí không đúng.");
            return "fail";
        }
    }
    public List<?> getPayments() {
        User user = SecurityUtils.getCurrentUser();
        if (user.getRole() == Role.ROLE_CUSTOMER){
            return paymentMapper.toPaymentResponseNoUserList(paymentRepository.findByCustomer_Id(user.getId()));
        } else if (user.getRole() == Role.ROLE_ADMIN) {
            return paymentMapper.toPaymentResponseList(paymentRepository.findAll());
        } else {
            return null;
        }
    }
    public PaymentResponse getAPayment(long id){
        User user = SecurityUtils.getCurrentUser();
        if (user.getRole() == Role.ROLE_CUSTOMER){
            Optional<Payment> paymentOptional = paymentRepository.findByIdAndCustomerId(id, user.getId());
            if (paymentOptional.isPresent()){
                return paymentMapper.toPaymentResponse(paymentOptional.get());
            }
        } else if (user.getRole() == Role.ROLE_ADMIN) {
            Optional<Payment> paymentOptional = paymentRepository.findById(id);
            if (paymentOptional.isPresent()){
                return paymentMapper.toPaymentResponse(paymentOptional.get());
            }
        }
        return null;
    }
    public BookingResponseAdmin cashPayment(long id){
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking!=null){
            if (booking.getStatus() == BookingStatus.PENDING){
                booking.setStatus(BookingStatus.PAID);
                Payment payment = new Payment();
                List<BookingDetail> bookingDetails = booking.getBookingDetails();
                long temp_price = 0;
                for (BookingDetail bookingDetail : bookingDetails){
                    if (bookingDetail.getService() != null) {
                        temp_price += bookingDetail.getService().getPrice();
                        bookingDetail.setFinalPrice(bookingDetail.getService().getPrice());
                    }
                    if (bookingDetail.getCombo() != null) {
                        temp_price += bookingDetail.getCombo().getPrice();
                        bookingDetail.setFinalPrice(bookingDetail.getCombo().getPrice());
                    }
                }
                booking.setTotalPrice(temp_price);
                payment.setAmount(temp_price);
                payment.setPaid_at(new Timestamp(System.currentTimeMillis()));
                payment.setCardType("CASH");
                paymentRepository.save(payment);
                booking.setPayment(payment);
                bookingRepository.save(booking);
                return bookingMapper.toResponseAdmin(booking);
            } else {
                throw new LocalizedException("cash.pay.on.pending.only");
            }
        } else {
            throw new LocalizedException("booking.not.found");
        }
    }
}

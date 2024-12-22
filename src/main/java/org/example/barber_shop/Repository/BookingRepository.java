package org.example.barber_shop.Repository;


import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.DTO.User.CustomerTopBookings;
import org.example.barber_shop.DTO.User.StaffTopBookings;
import org.example.barber_shop.Entity.Booking;
import org.example.barber_shop.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomer_Id(Long customerId);
    List<Booking> findByStaff_IdAndStatusAndStartTimeBeforeAndEndTimeAfter(Long staff_id, BookingStatus status, Timestamp startTime, Timestamp endTime);
    List<Booking> findByStaff_IdAndStartTimeBetweenAndStatus(Long staff_id, Timestamp startTime, Timestamp endTime, BookingStatus status);
    List<Booking> findAllByIdInAndCustomer_IdAndStatus(List<Long> id, Long customerId, BookingStatus status);
    List<Booking> findByIdIn(List<Long> id);
    Booking findByIdAndStatusAndStaff_Id(Long id, BookingStatus status, Long staffId);
    List<Booking> findByStaff_Id(Long staffId);
    List<Booking> findByStatusAndStartTimeBetween(BookingStatus bookingStatus, Timestamp startTime, Timestamp endTime);
    Booking findByIdAndCustomerAndStatus(Long id, User customer, BookingStatus status);
    List<Booking> findAllByStatus(BookingStatus status);
    Booking findByIdAndCustomer(long id, User customer);
    Booking findByIdAndStaff(long id, User staff);
    List<Booking> findByStaffIn(Collection<User> staff);
    int countAllByCustomerAndStartTimeBetween(User customer, Timestamp startTime, Timestamp endTime);

    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.customer.id = :id AND b.status = 'PAID' and b.startTime >= :monthStart and b.startTime <= :monthEnd")
    Long sumTotalPrice(@Param("id") long id, @Param("monthStart") Timestamp monthStart, @Param("monthEnd") Timestamp monthEnd);
    List<Booking> findByStaffAndStatusNotAndStartTimeGreaterThanOrEndTimeLessThanOrStartTimeLessThanAndEndTimeGreaterThan(User staff, BookingStatus status, Timestamp startTime, Timestamp endTime, Timestamp startTime2, Timestamp endTime2);
    List<Booking> findByStaffAndStatusInAndStartTimeGreaterThanAndEndTimeLessThan(User staff, Collection<BookingStatus> status, Timestamp startTime, Timestamp endTime);

    @Query("select new org.example.barber_shop.DTO.User.CustomerTopBookings(b.customer.id, sum(b.totalPrice), count(b.id)) from Booking b where b.status = 'COMPLETED' or b.status = 'PAID' group by b.customer.id")
    List<CustomerTopBookings> findTopCustomers();

    @Query("select new org.example.barber_shop.DTO.User.CustomerTopBookings(b.customer.id, sum(b.totalPrice), count(b.id)) from Booking b where (b.status = 'COMPLETED' or b.status = 'PAID') and b.startTime >= :from and b.endTime <= :to group by b.customer.id")
    List<CustomerTopBookings> findTopCustomers(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select function('MONTH', b.startTime) as month, count(b.id) as bookingCount, sum(b.totalPrice) as totalAmount from Booking b where (b.status = 'COMPLETED' or b.status = 'PAID') AND FUNCTION('YEAR', b.startTime) = :year group by FUNCTION('MONTH', b.startTime)")
    List<Object[]> getMonthlyStatistics(@Param("year") int year);

    @Query("select new org.example.barber_shop.DTO.User.StaffTopBookings(b.staff.id, sum(b.totalPrice), count(b.id)) from Booking b where b.status = 'COMPLETED' or b.status = 'PAID' group by b.staff.id")
    List<StaffTopBookings> findTopStaffs();
    @Query("select new org.example.barber_shop.DTO.User.StaffTopBookings(b.staff.id, sum(b.totalPrice), count(b.id)) from Booking b where (b.status = 'COMPLETED' or b.status = 'PAID') and b.startTime >= :from and b.endTime <= :to group by b.staff.id")
    List<StaffTopBookings> findTopStaffs(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    List<Booking> findByCustomer_IdAndStartTimeGreaterThanAndEndTimeLessThan(Long customer_id, Timestamp startTime, Timestamp endTime);
}

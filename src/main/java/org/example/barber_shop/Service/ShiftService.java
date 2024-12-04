package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.BookingStatus;
import org.example.barber_shop.Constants.Role;
import org.example.barber_shop.DTO.Shift.*;
import org.example.barber_shop.Entity.Booking;
import org.example.barber_shop.Entity.Shift;
import org.example.barber_shop.Entity.StaffShift;
import org.example.barber_shop.Entity.User;
import org.example.barber_shop.Mapper.StaffShiftMapper;
import org.example.barber_shop.Repository.BookingRepository;
import org.example.barber_shop.Repository.ShiftRepository;
import org.example.barber_shop.Repository.StaffShiftRepository;
import org.example.barber_shop.Repository.UserRepository;
import org.example.barber_shop.Util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShiftService {
    private final ShiftRepository shiftRepository;
    private final StaffShiftRepository staffShiftRepository;
    private final UserRepository userRepository;
    private final StaffShiftMapper staffShiftMapper;
    private final BookingRepository bookingRepository;

    public List<StaffShiftResponse> addStaffShift(StaffShiftRequest shiftRequest) {
        for (int i = 0; i < shiftRequest.dates.size(); i++) {
            if (shiftRequest.dates.get(i).isBefore(LocalDate.now())) {
                throw new RuntimeException("Invalid date, all dates must be in the future.");
            }
        }
        Optional<Shift> shift = shiftRepository.findById(shiftRequest.shiftId);
        if (shift.isPresent()) {
            Shift checkedShift = shift.get();
            User user = userRepository.findByIdAndRole(shiftRequest.staffId, Role.ROLE_STAFF);
            if (user != null){
                List<StaffShift> staffShiftCheck = staffShiftRepository.findByStaffIdAndDateIn(user.getId(), shiftRequest.dates);
                for (int i = 0; i < staffShiftCheck.size(); i++) {
                    if (staffShiftCheck.get(i).getStartTime() == checkedShift.getStartTime() && staffShiftCheck.get(i).getEndTime() == checkedShift.getEndTime()) {
                        throw new RuntimeException("Staff already have this shift.");
                    }
                }
                ArrayList<StaffShift> staffShifts = new ArrayList<>();
                for (int i = 0; i < shiftRequest.dates.size(); i++) {
                    StaffShift staffShift = new StaffShift();
                    staffShift.setStaff(user);
                    staffShift.setShift(checkedShift);
                    staffShift.setStartTime(checkedShift.getStartTime());
                    staffShift.setEndTime(checkedShift.getEndTime());
                    staffShift.setDate(shiftRequest.dates.get(i));
                    staffShifts.add(staffShift);
                }

                return staffShiftMapper.toStaffShiftResponses(staffShiftRepository.saveAll(staffShifts));
            } else {
                throw new RuntimeException("Invalid staff id.");
            }
        } else {
            throw new RuntimeException("Invalid shift id.");
        }
    }
    public List<Shift> getAllShift(){
        return shiftRepository.findAll();
    }
    public List<StaffShiftResponse> staffGetShifts(Integer week, Integer year, long staff_id){
        if (week == null && year == null) {
            LocalDate today = LocalDate.now();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            week = today.get(weekFields.weekOfYear());
            year = today.getYear();
        } else if (week == null || year == null) {
            throw new RuntimeException("Both week and year must be provided or neither.");
        }
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startDate = LocalDate.ofYearDay(year, 1)
                .with(weekFields.weekOfYear(), week)
                .with(weekFields.dayOfWeek(), 1); // Monday
        LocalDate endDate = startDate.plusDays(6); // Sunday
        return staffShiftMapper.toStaffShiftResponses(staffShiftRepository.findByStaffIdAndDateBetween(staff_id, startDate, endDate));
    }
    public List<AdminShiftResponse> adminGetShiftsInWeek(Integer week, Integer year){
        if (week == null && year == null) {
            LocalDate today = LocalDate.now();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            week = today.get(weekFields.weekOfYear());
            year = today.getYear();
        } else if (week == null || year == null) {
            throw new RuntimeException("Both week and year must be provided or neither.");
        }
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate startDate = LocalDate.ofYearDay(year, 1)
                .with(weekFields.weekOfYear(), week)
                .with(weekFields.dayOfWeek(), 1); // Monday
        LocalDate endDate = startDate.plusDays(6); // Sunday
        return staffShiftMapper.toAdminShiftResponses(staffShiftRepository.findByDateBetween(startDate, endDate));
    }
    public Shift updateShift(ShiftRequest shiftRequest){
        Optional<Shift> shift = shiftRepository.findById(shiftRequest.id);
        if (shift.isPresent()) {
            Shift checkedShift = shift.get();
            checkedShift.setName(shiftRequest.name);
            checkedShift.setStartTime(shiftRequest.startTime);
            checkedShift.setEndTime(shiftRequest.endTime);
            return shiftRepository.save(checkedShift);
        } else {
            throw new RuntimeException("Invalid shift id.");
        }
    }
    public Shift addShift(AddShiftRequest addShiftRequest){
        Shift shift = new Shift();
        shift.setName(addShiftRequest.name);
        shift.setStartTime(addShiftRequest.startTime);
        shift.setEndTime(addShiftRequest.endTime);
        return shiftRepository.save(shift);
    }
    public boolean deleteShift(long shiftId){
        Optional<Shift> shift = shiftRepository.findById(shiftId);
        if (shift.isPresent()) {
            shiftRepository.delete(shift.get());
            return true;
        } else {
            return false;
        }
    }
    public static Timestamp convertToTimestamp(LocalDate date, LocalTime time) {
        if (date == null || time == null) {
            throw new IllegalArgumentException("Date and time must not be null");
        }
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        return Timestamp.valueOf(dateTime);
    }
    public boolean delete(long id){
        Optional<StaffShift> staffShiftOptional = staffShiftRepository.findById(id);
        if (staffShiftOptional.isPresent()) {
            StaffShift staffShift = staffShiftOptional.get();
            User staff = staffShift.getStaff();
            Timestamp startTime = convertToTimestamp(staffShift.getDate(), staffShift.getStartTime());
            Timestamp endTime = convertToTimestamp(staffShift.getDate(), staffShift.getEndTime());
            List<Timestamp> timestamps = List.of(startTime, endTime);
            List<Booking> bookings = bookingRepository.findByStaffAndStatusNotAndStartTimeInOrEndTimeInOrStartTimeLessThanAndEndTimeGreaterThan(staff, BookingStatus.PENDING, timestamps, timestamps, startTime, endTime);
            if (bookings.isEmpty()) {
                staffShiftRepository.delete(staffShift);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}

package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.Role;
import org.example.barber_shop.DTO.Shift.AdminShiftResponse;
import org.example.barber_shop.DTO.Shift.ShiftRequest;
import org.example.barber_shop.DTO.Shift.StaffShiftRequest;
import org.example.barber_shop.DTO.Shift.StaffShiftResponse;
import org.example.barber_shop.Entity.Shift;
import org.example.barber_shop.Entity.StaffShift;
import org.example.barber_shop.Entity.User;
import org.example.barber_shop.Mapper.StaffShiftMapper;
import org.example.barber_shop.Repository.ShiftRepository;
import org.example.barber_shop.Repository.StaffShiftRepository;
import org.example.barber_shop.Repository.UserRepository;
import org.example.barber_shop.Util.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
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

    public StaffShiftResponse addStaffShift(StaffShiftRequest shiftRequest) {
        Optional<Shift> shift = shiftRepository.findById(shiftRequest.shiftId);
        if (shift.isPresent()) {
            Shift checkedShift = shift.get();
            User user = userRepository.findByIdAndRole(shiftRequest.staffId, Role.ROLE_STAFF);
            if (user != null){
                StaffShift checkExist = staffShiftRepository.findByDateAndShift(shiftRequest.date, checkedShift);
                if (checkExist == null){
                    StaffShift staffShift = new StaffShift();
                    staffShift.setStaff(user);
                    staffShift.setShift(checkedShift);
                    staffShift.setStartTime(checkedShift.getStartTime());
                    staffShift.setEndTime(checkedShift.getEndTime());
                    staffShift.setDate(shiftRequest.date);
                    staffShift = staffShiftRepository.save(staffShift);
                    return staffShiftMapper.toStaffShiftResponse(staffShift);
                } else {
                    throw new RuntimeException("Already have a staff working in this shift.");
                }
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
}

package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.Role;
import org.example.barber_shop.DTO.Salary.StaffSalaryUpdateRequest;
import org.example.barber_shop.DTO.Salary.StaffSalaryResponse;
import org.example.barber_shop.DTO.Salary.WeeklySalaryAdminResponse;
import org.example.barber_shop.DTO.Salary.WeeklySalaryStaffResponse;
import org.example.barber_shop.Entity.StaffSalary;
import org.example.barber_shop.Entity.User;
import org.example.barber_shop.Entity.WeeklySalary;
import org.example.barber_shop.Exception.LocalizedException;
import org.example.barber_shop.Mapper.StaffSalaryMapper;
import org.example.barber_shop.Mapper.WeeklySalaryMapper;
import org.example.barber_shop.Repository.StaffSalaryRepository;
import org.example.barber_shop.Repository.UserRepository;
import org.example.barber_shop.Repository.WeeklySalaryRepository;
import org.example.barber_shop.Util.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import static org.example.barber_shop.Util.TimeUtil.getEndOfWeek;
import static org.example.barber_shop.Util.TimeUtil.getStartOfWeek;


@Service
@RequiredArgsConstructor
public class SalaryService {
    private final StaffSalaryRepository staffSalaryRepository;
    private final UserRepository userRepository;
    private final StaffSalaryMapper staffSalaryMapper;
    private final WeeklySalaryRepository weeklySalaryRepository;
    private final WeeklySalaryMapper weeklySalaryMapper;

    public StaffSalaryResponse updateStaffSalary(StaffSalaryUpdateRequest staffSalaryUpdateRequest) {
        User staff = userRepository.findByIdAndRole(staffSalaryUpdateRequest.staff_id, Role.ROLE_STAFF);
        if (staff != null) {
            StaffSalary staffSalary = staffSalaryRepository.findByStaff(staff);
            if (staffSalary != null) {
                staffSalary.setStaff(staff);
                staffSalary.setRate(staffSalaryUpdateRequest.rate);
                staffSalary.setPercentage(staffSalaryUpdateRequest.percentage);
                return staffSalaryMapper.toStaffSalaryResponse(staffSalaryRepository.save(staffSalary));
            } else {
                throw new LocalizedException("staff.salary.error", staff.getName());
            }
        } else {
            throw new LocalizedException("staff.not.found");
        }
    }
    public List<StaffSalaryResponse> getAllStaffSalary(){
        return staffSalaryMapper.toStaffSalaryResponses(staffSalaryRepository.findAll());
    }
    public StaffSalaryResponse getAStaffSalary(long staff_id){
        User staff = userRepository.findByIdAndRole(staff_id, Role.ROLE_STAFF);
        if (staff != null) {
            StaffSalary staffSalary = staffSalaryRepository.findByStaff(staff);
            return staffSalaryMapper.toStaffSalaryResponse(staffSalary);
        } else {
            throw new LocalizedException("staff.not.found");
        }
    }
    public List<WeeklySalaryAdminResponse> adminGetWeeklySalary(Integer week, Integer year, Long staff_id){
        if (week == null && year == null) {
            LocalDate today = LocalDate.now();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            week = today.get(weekFields.weekOfYear());
            year = today.getYear();
        } else if (week == null || year == null) {
            throw new LocalizedException("week.year.required");
        }
        List<WeeklySalary> weeklySalaries;
        LocalDate weekStartDate = getStartOfWeek(week, year);
        LocalDate weekEndDate = getEndOfWeek(week, year);
        if (staff_id == null){
            weeklySalaries = weeklySalaryRepository.findByWeekStartDateAndWeekEndDate(weekStartDate, weekEndDate);
        } else {
            weeklySalaries = weeklySalaryRepository.findByStaff_IdAndWeekStartDateAndWeekEndDate(staff_id, weekStartDate, weekEndDate);
        }
        return weeklySalaryMapper.toWeeklySalaryAdminResponse(weeklySalaries);
    }
    public List<WeeklySalaryStaffResponse> staffGetWeeklySalary(Integer week, Integer year){
        if (week == null && year == null) {
            LocalDate today = LocalDate.now();
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            week = today.get(weekFields.weekOfYear());
            year = today.getYear();
        } else if (week == null || year == null) {
            throw new LocalizedException("week.year.required");
        }
        LocalDate weekStartDate = getStartOfWeek(week, year);
        LocalDate weekEndDate = getEndOfWeek(week, year);
        long staff_id = SecurityUtils.getCurrentUserId();
        List<WeeklySalary> weeklySalaries = weeklySalaryRepository.findByStaff_IdAndWeekStartDateAndWeekEndDate(staff_id, weekStartDate, weekEndDate);
        return weeklySalaryMapper.toWeeklySalaryStaffResponse(weeklySalaries);
    }
}

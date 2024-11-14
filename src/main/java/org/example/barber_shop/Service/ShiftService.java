package org.example.barber_shop.Service;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.Constants.Role;
import org.example.barber_shop.DTO.Shift.StaffShiftRequest;
import org.example.barber_shop.DTO.Shift.StaffShiftResponse;
import org.example.barber_shop.Entity.Shift;
import org.example.barber_shop.Entity.StaffShift;
import org.example.barber_shop.Entity.User;
import org.example.barber_shop.Mapper.StaffShiftMapper;
import org.example.barber_shop.Repository.ShiftRepository;
import org.example.barber_shop.Repository.StaffShiftRepository;
import org.example.barber_shop.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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
}

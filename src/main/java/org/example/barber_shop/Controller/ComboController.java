package org.example.barber_shop.Controller;

import lombok.RequiredArgsConstructor;
import org.example.barber_shop.DTO.ApiResponse;
import org.example.barber_shop.DTO.Combo.ComboRequest;
import org.example.barber_shop.DTO.Combo.ComboUpdateRequest;
import org.example.barber_shop.Service.ComboService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/combo")
@RequiredArgsConstructor
public class ComboController {
    private final ComboService comboService;

    @GetMapping("/get-all-combos")
    public ApiResponse<?> getAllCombo() {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "ALL COMBOS", comboService.getAllCombo()
        );
    }
    @GetMapping("/get-one-combo/{id}")
    public ApiResponse<?> getComboById(@PathVariable long id) {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "COMBO ID" + id, comboService.getComboById(id)
        );
    }

    @PostMapping(value = "/add-combo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> addCombo(@ModelAttribute ComboRequest comboRequest) throws IOException {
        return new ApiResponse<>(
                HttpStatus.CREATED.value(), "COMBO CREATED", comboService.addCombo(comboRequest)
        );
    }
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable long id){
        if (comboService.delete(id)){
            return new ApiResponse<>(
                    HttpStatus.CREATED.value(), "COMBO DELETED", null
            );
        } else {
            return new ApiResponse<>(
                    HttpStatus.UNPROCESSABLE_ENTITY.value(), "DELETE FAIL", null
            );
        }
    }
    @PutMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<?> update(@ModelAttribute ComboUpdateRequest comboUpdateRequest) throws IOException {
        return new ApiResponse<>(
                HttpStatus.OK.value(), "COMBO UPDATED", comboService.update(comboUpdateRequest)
        );
    }
}

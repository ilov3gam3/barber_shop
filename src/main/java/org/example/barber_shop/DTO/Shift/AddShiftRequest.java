package org.example.barber_shop.DTO.Shift;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;

import java.time.LocalTime;

public class AddShiftRequest {
    public String name;
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    public LocalTime startTime;
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    public LocalTime endTime;
}

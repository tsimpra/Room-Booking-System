package com.acme.rbs.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomBookingSearchDTO {
    @NotNull
    private Long roomId;
    @NotNull
    private LocalDate bookingDate;
    private int page = 0;
    private int size = 10;
}

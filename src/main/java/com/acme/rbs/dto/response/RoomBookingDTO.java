package com.acme.rbs.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

public record RoomBookingDTO(Long id, LocalDate bookingDate, LocalTime startTime, LocalTime endTime, Long userId,
                             String userMail) {
}

package com.acme.rbs.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;

public record BookingRequest(Long roomId, Long userId, LocalDate bookingDate, LocalTime startTime, LocalTime endTime) {
}

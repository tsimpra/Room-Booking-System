package com.acme.rbs.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record BookingRequest(@NotNull Long roomId, @NotNull Long userId, @NotNull LocalDate bookingDate,
                             @NotNull LocalTime startTime, @NotNull LocalTime endTime) {
}

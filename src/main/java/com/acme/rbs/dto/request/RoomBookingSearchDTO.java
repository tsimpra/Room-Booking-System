package com.acme.rbs.dto.request;

import java.time.LocalDate;

public record RoomBookingSearchDTO(String roomName, LocalDate bookingDate, int page, int size) {
    public RoomBookingSearchDTO(String roomName, LocalDate bookingDate) {
        this(roomName, bookingDate, 1, 10);
    }
}

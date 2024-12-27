package com.acme.rbs.service.validation;

import com.acme.rbs.dto.request.BookingRequest;
import com.acme.rbs.dto.request.RoomBookingSearchDTO;

public interface ValidationService {

    void validateSearchRequest(RoomBookingSearchDTO roomBookingSearchDTO);

    void validateBookingRequest(BookingRequest bookingRequest);

    void validateCancellationRequest(Long roomBookingId);
}

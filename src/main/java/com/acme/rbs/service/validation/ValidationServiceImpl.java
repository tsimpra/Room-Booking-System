package com.acme.rbs.service.validation;

import com.acme.rbs.dto.request.BookingRequest;
import com.acme.rbs.dto.request.RoomBookingSearchDTO;
import com.acme.rbs.exception.BookingRequestException;
import com.acme.rbs.exception.CancellationException;
import com.acme.rbs.exception.RoomNotFoundException;
import com.acme.rbs.repository.AcmeUserRepository;
import com.acme.rbs.repository.RoomBookingRepository;
import com.acme.rbs.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValidationServiceImpl implements ValidationService {
    private final RoomRepository roomRepository;
    private final RoomBookingRepository roomBookingRepository;
    private final AcmeUserRepository acmeUserRepository;

    @Override
    public void validateSearchRequest(RoomBookingSearchDTO roomBookingSearchDTO) {
        log.debug("Triggering validation for RoomBookingSearchDTO: {}", roomBookingSearchDTO);
        if (roomNameDoesNotExist(roomBookingSearchDTO.getRoomId())) {
            String errorMsg = String.format("Room with id:%s does not exist", roomBookingSearchDTO.getRoomId());
            log.error(errorMsg);
            throw new RoomNotFoundException(errorMsg);
        }
        log.debug("Validation successful");
    }

    @Override
    public void validateBookingRequest(BookingRequest bookingRequest) {
        log.debug("Triggering validation for BookingRequest: {}", bookingRequest);
        if (userDoesNotExist(bookingRequest)) {
            String errorMsg = String.format("No user found with id:%s", bookingRequest.userId());
            log.error(errorMsg);
            throw new BookingRequestException(errorMsg);
        }
        if (roomDoesNotExist(bookingRequest)) {
            String errorMsg = String.format("No room found with id:%s", bookingRequest.roomId());
            log.error(errorMsg);
            throw new BookingRequestException(errorMsg);
        }
        if (isBookingDatePriorToNow(bookingRequest)) {
            String errorMsg = "Booking date is prior to current date.";
            log.error(errorMsg);
            throw new BookingRequestException(errorMsg);
        }
        if (hasInvalidStartEndTime(bookingRequest)) {
            String errorMsg = "Start time and/or end time are not valid.";
            log.error(errorMsg);
            throw new BookingRequestException(errorMsg);
        }
        if (isOverlappingAnotherBooking(bookingRequest)) {
            String errorMsg = "Booking request is overlapping another booking.";
            log.error(errorMsg);
            throw new BookingRequestException(errorMsg);
        }
        log.debug("Validation successful");
    }

    @Override
    public void validateCancellationRequest(Long roomBookingId) {
        log.debug("Triggering validation for Room Booking Cancellation with id: {}", roomBookingId);
        roomBookingRepository.findById(roomBookingId)
                .ifPresent(roomBooking -> {
                    //improve logic with hours
                    if (LocalDate.now(ZoneOffset.UTC).isAfter(roomBooking.getBookingDate())) {
                        String errorMsg = "Cannot cancel and old booking";
                        log.error(errorMsg);
                        throw new CancellationException(errorMsg);
                    }
                });
        log.debug("Validation successful");
    }

    private boolean isBookingDatePriorToNow(BookingRequest bookingRequest) {
        //improve logic with hours
        return LocalDate.now(ZoneOffset.UTC).isAfter(bookingRequest.bookingDate());
    }

    private boolean roomDoesNotExist(BookingRequest bookingRequest) {
        return !roomRepository.existsById(bookingRequest.roomId());
    }

    private boolean userDoesNotExist(BookingRequest bookingRequest) {
        return acmeUserRepository.findById(bookingRequest.userId()).isEmpty();
    }

    private boolean hasInvalidStartEndTime(BookingRequest bookingRequest) {
        //if end time is not after return false
        if (!bookingRequest.endTime().isAfter(bookingRequest.startTime())) {
            return false;
        }
        //if end time minutes != start time minutes, it means that is not multiples of 1 hour
        //we also assume seconds are passed by FE as 00 or are irrelevant to our logic
        if (bookingRequest.endTime().getMinute() != bookingRequest.startTime().getMinute()) {
            return false;
        }
        //Since we have verified the minutes, we just need to verify that end time hours - start time hours >=1
        return bookingRequest.endTime().getHour() - bookingRequest.startTime().getHour() < 1;
    }

    private boolean isOverlappingAnotherBooking(BookingRequest bookingRequest) {
        return roomBookingRepository.existsByStartTimeIsBeforeAndEndTimeIsAfterAndBookingDateAndRoomId(
                bookingRequest.endTime(),
                bookingRequest.startTime(),
                bookingRequest.bookingDate(),
                bookingRequest.roomId());
    }

    private boolean roomNameDoesNotExist(Long roomId) {
        return !roomRepository.existsById(roomId);
    }
}

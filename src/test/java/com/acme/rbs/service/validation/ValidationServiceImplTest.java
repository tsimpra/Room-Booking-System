package com.acme.rbs.service.validation;

import com.acme.rbs.domain.AcmeUser;
import com.acme.rbs.domain.Room;
import com.acme.rbs.domain.RoomBooking;
import com.acme.rbs.dto.request.BookingRequest;
import com.acme.rbs.dto.request.RoomBookingSearchDTO;
import com.acme.rbs.exception.BookingRequestException;
import com.acme.rbs.exception.CancellationException;
import com.acme.rbs.exception.RoomNotFoundException;
import com.acme.rbs.repository.AcmeUserRepository;
import com.acme.rbs.repository.RoomBookingRepository;
import com.acme.rbs.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

    @Mock
    RoomRepository roomRepository;
    @Mock
    RoomBookingRepository roomBookingRepository;
    @Mock
    AcmeUserRepository acmeUserRepository;
    @InjectMocks
    private ValidationServiceImpl validationService;

    @Test
    void validateSearchRequest_valid_whenRoomExists() {
        //Initialize DTO
        Long existingRoomId = 456L;
        RoomBookingSearchDTO roomBookingSearchDTO = new RoomBookingSearchDTO();
        roomBookingSearchDTO.setRoomId(existingRoomId);

        //mock functionality
        when(roomRepository.existsById(existingRoomId)).thenReturn(true);

        //trigger
        validationService.validateSearchRequest(roomBookingSearchDTO);

        //verify
        verify(roomRepository, times(1)).existsById(existingRoomId);
    }

    @Test
    void validateSearchRequest_invalid_whenRoomDoesNotExist() {
        //Initialize DTO
        Long roomId = 123L;
        RoomBookingSearchDTO roomBookingSearchDTO = new RoomBookingSearchDTO();
        roomBookingSearchDTO.setRoomId(roomId);

        //mock functionality
        when(roomRepository.existsById(roomId)).thenReturn(false);

        //trigger and verify error
        assertThrows(
                RoomNotFoundException.class,
                () -> validationService.validateSearchRequest(roomBookingSearchDTO)
        );

        //verify
        verify(roomRepository, times(1)).existsById(roomId);
    }

    @Test
    void validateBookingRequest_valid() {
        //Initialize DTO
        BookingRequest bookingRequest = new BookingRequest(0L, 0L,
                LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));

        //mock functionality
        when(acmeUserRepository.findById(bookingRequest.userId())).thenReturn(Optional.of(new AcmeUser()));
        when(roomRepository.existsById(bookingRequest.roomId())).thenReturn(true);
        when(roomBookingRepository.existsByStartTimeIsBeforeAndEndTimeIsAfterAndBookingDateAndRoomId(
                bookingRequest.endTime(), bookingRequest.startTime(), bookingRequest.bookingDate(), bookingRequest.roomId()))
                .thenReturn(false);

        //trigger
        validationService.validateBookingRequest(bookingRequest);

        //verify
        verify(acmeUserRepository, times(1)).findById(bookingRequest.userId());
        verify(roomRepository, times(1)).existsById(bookingRequest.roomId());
        verify(roomBookingRepository, times(1)).existsByStartTimeIsBeforeAndEndTimeIsAfterAndBookingDateAndRoomId(
                bookingRequest.endTime(), bookingRequest.startTime(), bookingRequest.bookingDate(), bookingRequest.roomId());
        assertFalse(LocalDate.now(ZoneOffset.UTC).isAfter(bookingRequest.bookingDate()));
        assertTrue(bookingRequest.endTime().isAfter(bookingRequest.startTime()));
        assertEquals(bookingRequest.endTime().getMinute(), bookingRequest.startTime().getMinute());
    }

    @Test
    void validateBookingRequest_invalid_userDoesNotExist() {
        //Initialize DTO
        BookingRequest bookingRequest = new BookingRequest(0L, 0L,
                LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));

        //mock functionality
        when(acmeUserRepository.findById(bookingRequest.userId())).thenReturn(Optional.empty());

        //trigger and verify error
        assertThrows(BookingRequestException.class,
                () -> validationService.validateBookingRequest(bookingRequest));

        //verify
        verify(acmeUserRepository, times(1)).findById(bookingRequest.userId());
    }

    @Test
    void validateBookingRequest_invalid_roomDoesNotExist() {
        //Initialize DTO
        BookingRequest bookingRequest = new BookingRequest(0L, 0L,
                LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));

        //mock functionality
        when(acmeUserRepository.findById(bookingRequest.userId())).thenReturn(Optional.of(new AcmeUser()));
        when(roomRepository.existsById(bookingRequest.roomId())).thenReturn(false);

        //trigger and verify error
        assertThrows(BookingRequestException.class,
                () -> validationService.validateBookingRequest(bookingRequest));

        //verify
        verify(acmeUserRepository, times(1)).findById(bookingRequest.userId());
        verify(roomRepository, times(1)).existsById(bookingRequest.roomId());
    }

    @Test
    void validateBookingRequest_invalid_bookingDateIsPriorToNow() {
        //Initialize DTO
        BookingRequest bookingRequest = new BookingRequest(0L, 0L,
                LocalDate.now().minusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));

        //mock functionality
        when(acmeUserRepository.findById(bookingRequest.userId())).thenReturn(Optional.of(new AcmeUser()));
        when(roomRepository.existsById(bookingRequest.roomId())).thenReturn(true);

        //trigger and verify error
        assertThrows(BookingRequestException.class,
                () -> validationService.validateBookingRequest(bookingRequest));

        //verify
        verify(acmeUserRepository, times(1)).findById(bookingRequest.userId());
        verify(roomRepository, times(1)).existsById(bookingRequest.roomId());
        assertTrue(LocalDate.now(ZoneOffset.UTC).isAfter(bookingRequest.bookingDate()));
    }

    @Test
    void validateBookingRequest_invalid_endTimeIsPriorToStartTime() {
        //Initialize DTO
        BookingRequest bookingRequest = new BookingRequest(0L, 0L,
                LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(8, 0));

        //mock functionality
        when(acmeUserRepository.findById(bookingRequest.userId())).thenReturn(Optional.of(new AcmeUser()));
        when(roomRepository.existsById(bookingRequest.roomId())).thenReturn(true);

        //trigger and verify error
        assertThrows(BookingRequestException.class,
                () -> validationService.validateBookingRequest(bookingRequest));

        //verify
        verify(acmeUserRepository, times(1)).findById(bookingRequest.userId());
        verify(roomRepository, times(1)).existsById(bookingRequest.roomId());
        assertFalse(LocalDate.now(ZoneOffset.UTC).isAfter(bookingRequest.bookingDate()));
        assertFalse(bookingRequest.endTime().isAfter(bookingRequest.startTime()));
    }

    @Test
    void validateBookingRequest_invalid_endTimeIsEqualToStartTime() {
        //Initialize DTO
        BookingRequest bookingRequest = new BookingRequest(0L, 0L,
                LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(10, 0));

        //mock functionality
        when(acmeUserRepository.findById(bookingRequest.userId())).thenReturn(Optional.of(new AcmeUser()));
        when(roomRepository.existsById(bookingRequest.roomId())).thenReturn(true);

        //trigger and verify error
        assertThrows(BookingRequestException.class,
                () -> validationService.validateBookingRequest(bookingRequest));

        //verify
        verify(acmeUserRepository, times(1)).findById(bookingRequest.userId());
        verify(roomRepository, times(1)).existsById(bookingRequest.roomId());
        assertFalse(LocalDate.now(ZoneOffset.UTC).isAfter(bookingRequest.bookingDate()));
        assertFalse(bookingRequest.endTime().isAfter(bookingRequest.startTime()));
    }

    @Test
    void validateBookingRequest_invalid_minutesDoNotMatch() {
        //Initialize DTO
        BookingRequest bookingRequest = new BookingRequest(0L, 0L,
                LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 10));

        //mock functionality
        when(acmeUserRepository.findById(bookingRequest.userId())).thenReturn(Optional.of(new AcmeUser()));
        when(roomRepository.existsById(bookingRequest.roomId())).thenReturn(true);

        //trigger and verify error
        assertThrows(BookingRequestException.class,
                () -> validationService.validateBookingRequest(bookingRequest));

        //verify
        verify(acmeUserRepository, times(1)).findById(bookingRequest.userId());
        verify(roomRepository, times(1)).existsById(bookingRequest.roomId());
        assertFalse(LocalDate.now(ZoneOffset.UTC).isAfter(bookingRequest.bookingDate()));
        assertTrue(bookingRequest.endTime().isAfter(bookingRequest.startTime()));
        assertNotEquals(bookingRequest.endTime().getMinute(), bookingRequest.startTime().getMinute());
    }

    @Test
    void validateBookingRequest_invalid_isOverlapping() {
        //Initialize DTO
        BookingRequest bookingRequest = new BookingRequest(0L, 0L,
                LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(12, 0));

        //mock functionality
        when(acmeUserRepository.findById(bookingRequest.userId())).thenReturn(Optional.of(new AcmeUser()));
        when(roomRepository.existsById(bookingRequest.roomId())).thenReturn(true);
        when(roomBookingRepository.existsByStartTimeIsBeforeAndEndTimeIsAfterAndBookingDateAndRoomId(
                bookingRequest.endTime(), bookingRequest.startTime(), bookingRequest.bookingDate(), bookingRequest.roomId()))
                .thenReturn(true);

        //trigger and verify error
        assertThrows(BookingRequestException.class,
                () -> validationService.validateBookingRequest(bookingRequest));

        //verify
        verify(acmeUserRepository, times(1)).findById(bookingRequest.userId());
        verify(roomRepository, times(1)).existsById(bookingRequest.roomId());
        verify(roomBookingRepository, times(1)).existsByStartTimeIsBeforeAndEndTimeIsAfterAndBookingDateAndRoomId(
                bookingRequest.endTime(), bookingRequest.startTime(), bookingRequest.bookingDate(), bookingRequest.roomId());
        assertFalse(LocalDate.now(ZoneOffset.UTC).isAfter(bookingRequest.bookingDate()));
        assertTrue(bookingRequest.endTime().isAfter(bookingRequest.startTime()));
        assertEquals(bookingRequest.endTime().getMinute(), bookingRequest.startTime().getMinute());
    }

    @Test
    void validateCancellationRequest_valid_roomBookingExistsAndDateIsNotBeforeNow() {
        //init
        Long roomBookingId = 0L;
        RoomBooking roomBooking = new RoomBooking(roomBookingId, LocalDate.now(), LocalTime.now(), LocalTime.now(),
                new Room(), new AcmeUser(), LocalDateTime.now(), LocalDateTime.now());

        //mock functionality
        when(roomBookingRepository.findById(roomBookingId)).thenReturn(Optional.of(roomBooking));

        //trigger
        validationService.validateCancellationRequest(roomBookingId);

        //verify
        verify(roomBookingRepository, times(1)).findById(roomBookingId);
    }

    @Test
    void validateCancellationRequest_invalid_roomBookingExists_dateIsBeforeNow() {
        //init
        Long roomBookingId = 0L;
        RoomBooking roomBooking = new RoomBooking(roomBookingId, LocalDate.now().minusDays(10), LocalTime.now(), LocalTime.now(),
                new Room(), new AcmeUser(), LocalDateTime.now(), LocalDateTime.now());

        //mock functionality
        when(roomBookingRepository.findById(roomBookingId)).thenReturn(Optional.of(roomBooking));

        //trigger and verify error
        assertThrows(CancellationException.class,
                () -> validationService.validateCancellationRequest(roomBookingId));

        //verify
        verify(roomBookingRepository, times(1)).findById(roomBookingId);
    }

    @Test
    void validateCancellationRequest_valid_roomBookingDoesNotExist() {
        //init
        Long roomBookingId = 0L;

        //mock functionality
        when(roomBookingRepository.findById(roomBookingId)).thenReturn(Optional.empty());

        //trigger
        validationService.validateCancellationRequest(roomBookingId);

        //verify
        verify(roomBookingRepository, times(1)).findById(roomBookingId);
    }
}
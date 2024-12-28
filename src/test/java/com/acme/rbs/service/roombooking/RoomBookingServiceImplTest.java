package com.acme.rbs.service.roombooking;

import com.acme.rbs.domain.RoomBooking;
import com.acme.rbs.domain.RoomBooking_;
import com.acme.rbs.domain.Room_;
import com.acme.rbs.domain.mapper.RoomBookingMapper;
import com.acme.rbs.domain.mapper.RoomBookingMapperImpl;
import com.acme.rbs.domain.mapper.RoomMapper;
import com.acme.rbs.domain.mapper.RoomMapperImpl;
import com.acme.rbs.dto.request.BookingRequest;
import com.acme.rbs.dto.request.RoomBookingSearchDTO;
import com.acme.rbs.dto.response.RoomBookingDTO;
import com.acme.rbs.repository.RoomBookingRepository;
import com.acme.rbs.repository.RoomRepository;
import com.acme.rbs.service.validation.ValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomBookingServiceImplTest {
    @Mock
    RoomRepository roomRepository;
    @Mock
    RoomBookingRepository roomBookingRepository;
    @Mock
    RoomMapper roomMapper = new RoomMapperImpl();
    @Mock
    RoomBookingMapper roomBookingMapper = new RoomBookingMapperImpl();
    @Mock
    ValidationService validationService;
    @InjectMocks
    RoomBookingServiceImpl roomBookingService;

    @Test
    void getRoomsList() {
        //mock functionality
        when(roomRepository.findAll(Sort.by(Sort.Order.asc(Room_.NAME)))).thenReturn(List.of());

        //trigger
        roomBookingService.getRoomsList();

        //verify
        verify(roomRepository, times(1)).findAll(Sort.by(Sort.Order.asc(Room_.NAME)));
    }

    @Test
    void getRoomBookingsByCriteria() {
        //initialize DTO
        RoomBookingSearchDTO roomBookingSearchDTO = new RoomBookingSearchDTO();

        //mock
        when(roomBookingRepository.findByBookingDateAndRoomId(
                roomBookingSearchDTO.getBookingDate(),
                roomBookingSearchDTO.getRoomId(),
                PageRequest.of(roomBookingSearchDTO.getPage(), roomBookingSearchDTO.getSize(),
                        Sort.by(Sort.Order.asc(RoomBooking_.START_TIME)))))
                .thenReturn(Page.empty());

        //trigger
        roomBookingService.getRoomBookingsByCriteria(roomBookingSearchDTO);

        //verify
        verify(validationService, times(1)).validateSearchRequest(roomBookingSearchDTO);
        verify(roomBookingRepository, times(1)).findByBookingDateAndRoomId(
                roomBookingSearchDTO.getBookingDate(),
                roomBookingSearchDTO.getRoomId(),
                PageRequest.of(roomBookingSearchDTO.getPage(), roomBookingSearchDTO.getSize(),
                        Sort.by(Sort.Order.asc(RoomBooking_.START_TIME))));
    }

    @Test
    void createRoomBooking() {
        //intialize DTO
        BookingRequest bookingRequest = new BookingRequest(0L, 0L, LocalDate.now(), LocalTime.now(), LocalTime.now());
        RoomBooking roomBooking = new RoomBooking();
        RoomBookingDTO roomBookingDTO = new RoomBookingDTO(1L, LocalDate.now(), LocalTime.now(), LocalTime.now(), 1L, "x@x.x");

        //mock
        when(roomBookingMapper.fromRequest(bookingRequest)).thenReturn(roomBooking);
        when(roomBookingRepository.save(roomBooking)).thenReturn(roomBooking);
        when(roomBookingMapper.toDto(roomBooking)).thenReturn(roomBookingDTO);

        //trigger
        roomBookingService.createRoomBooking(bookingRequest);

        //verify
        verify(validationService, times(1)).validateBookingRequest(bookingRequest);
        verify(roomBookingMapper, times(1)).fromRequest(bookingRequest);
        verify(roomBookingRepository, times(1)).save(roomBooking);
        verify(roomBookingMapper, times(1)).toDto(roomBooking);
    }

    @Test
    void cancelRoomBooking() {
        //init
        Long roomBookingId = 1L;

        //trigger
        roomBookingService.cancelRoomBooking(roomBookingId);

        //verify
        verify(validationService, times(1)).validateCancellationRequest(roomBookingId);
        verify(roomBookingRepository, times(1)).deleteById(roomBookingId);
    }
}
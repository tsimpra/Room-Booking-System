package com.acme.rbs.service.roombooking;

import com.acme.rbs.domain.RoomBooking;
import com.acme.rbs.domain.RoomBooking_;
import com.acme.rbs.domain.Room_;
import com.acme.rbs.domain.mapper.RoomBookingMapper;
import com.acme.rbs.domain.mapper.RoomMapper;
import com.acme.rbs.dto.request.BookingRequest;
import com.acme.rbs.dto.request.RoomBookingSearchDTO;
import com.acme.rbs.dto.response.RoomBookingDTO;
import com.acme.rbs.dto.response.RoomDTO;
import com.acme.rbs.dto.response.pagination.PageDTO;
import com.acme.rbs.repository.RoomBookingRepository;
import com.acme.rbs.repository.RoomRepository;
import com.acme.rbs.service.validation.ValidationService;
import com.acme.rbs.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomBookingServiceImpl implements RoomBookingService {

    private final RoomRepository roomRepository;
    private final RoomBookingRepository roomBookingRepository;
    private final RoomMapper roomMapper;
    private final RoomBookingMapper roomBookingMapper;
    private final ValidationService validationService;

    @Override
    public List<RoomDTO> getRoomsList() {
        return roomRepository.findAll(Sort.by(Sort.Order.asc(Room_.NAME)))
                .stream()
                .map(roomMapper::toDto)
                .toList();
    }

    @Override
    public PageDTO<RoomBookingDTO> getRoomBookingsByCriteria(RoomBookingSearchDTO roomBookingSearchDTO) {
        validationService.validateSearchRequest(roomBookingSearchDTO);

        log.info("Querying room bookings with payload: {}", roomBookingSearchDTO);
        Page<RoomBookingDTO> bookingDatesPage = roomBookingRepository.findByBookingDateAndRoomId(
                roomBookingSearchDTO.getBookingDate(),
                roomBookingSearchDTO.getRoomId(),
                PageRequest.of(roomBookingSearchDTO.getPage(), roomBookingSearchDTO.getSize(),
                        Sort.by(Sort.Order.asc(RoomBooking_.START_TIME))));

        return PageUtils.pageDTOFactory(bookingDatesPage, Streamable::toList);
    }

    @Override
    public RoomBookingDTO createRoomBooking(BookingRequest bookingRequest) {
        validationService.validateBookingRequest(bookingRequest);
        log.info("Persisting into database BookingRequest: {}", bookingRequest);
        RoomBooking entity = roomBookingRepository.save(roomBookingMapper.fromRequest(bookingRequest));
        log.info("Entity was persisted successfully");
        return roomBookingMapper.toDto(entity);
    }

    @Override
    public void cancelRoomBooking(Long roomBookingId) {
        validationService.validateCancellationRequest(roomBookingId);
        log.info("Removing Room booking with id: {}", roomBookingId);
        roomBookingRepository.deleteById(roomBookingId);
        log.info("Room Booking has been removed successfully");
    }
}

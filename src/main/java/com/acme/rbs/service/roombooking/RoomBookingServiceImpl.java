package com.acme.rbs.service.roombooking;

import com.acme.rbs.domain.RoomBooking;
import com.acme.rbs.domain.RoomBooking_;
import com.acme.rbs.domain.Room_;
import com.acme.rbs.domain.mapper.RoomBookingMapper;
import com.acme.rbs.domain.mapper.RoomMapper;
import com.acme.rbs.dto.request.RoomBookingSearchDTO;
import com.acme.rbs.dto.response.RoomBookingDTO;
import com.acme.rbs.dto.response.RoomDTO;
import com.acme.rbs.dto.response.pagination.PageDTO;
import com.acme.rbs.exception.RoomNotFoundException;
import com.acme.rbs.repository.RoomBookingRepository;
import com.acme.rbs.repository.RoomRepository;
import com.acme.rbs.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Override
    public List<RoomDTO> getRoomsList() {
        return roomRepository.findAll(Sort.by(Sort.Order.asc(Room_.NAME)))
                .stream()
                .map(roomMapper::toDto)
                .toList();
    }

    @Override
    public PageDTO<RoomBookingDTO> getRoomBookingsByCriteria(RoomBookingSearchDTO roomBookingSearchDTO) {
        validate(roomBookingSearchDTO);

        Page<RoomBooking> bookingDatesPage = roomBookingRepository.findByBookingDateAndRoomName(roomBookingSearchDTO.bookingDate(),
                roomBookingSearchDTO.roomName(),
                PageRequest.of(roomBookingSearchDTO.page(), roomBookingSearchDTO.size(), Sort.by(Sort.Order.asc(RoomBooking_.START_TIME))));

        return PageUtils.pageDTOFactory(bookingDatesPage,
                page -> page.stream().map(roomBookingMapper::toDto).toList());
    }

    private void validate(RoomBookingSearchDTO roomBookingSearchDTO) {
        if (roomNameDoesNotExist(roomBookingSearchDTO.roomName())) {
            String errorMsg = String.format("Room with name:%s does not exist", roomBookingSearchDTO.roomName());
            log.error(errorMsg);
            throw new RoomNotFoundException(errorMsg);
        }
    }

    private boolean roomNameDoesNotExist(String roomName) {
        return !roomRepository.existsByName(roomName);
    }
}

package com.acme.rbs.controller;

import com.acme.rbs.dto.request.RoomBookingSearchDTO;
import com.acme.rbs.dto.response.RoomBookingDTO;
import com.acme.rbs.dto.response.RoomDTO;
import com.acme.rbs.dto.response.pagination.PageDTO;
import com.acme.rbs.service.roombooking.RoomBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.acme.rbs.utils.EndpointConstants.GET_ROOMS_LIST;
import static com.acme.rbs.utils.EndpointConstants.ROOM_BOOKING;
import static com.acme.rbs.utils.EndpointConstants.ROOM_BOOKING_LIST;
import static com.acme.rbs.utils.EndpointConstants.ROOM_BOOKING_SYSTEM_API;
import static com.acme.rbs.utils.EndpointConstants.V1;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = ROOM_BOOKING_SYSTEM_API + V1 + ROOM_BOOKING)
public class RoomBookingController {

    private final RoomBookingService roomBookingService;


    //retrieve meeting room names for search list component, default ordering name asc
    @GetMapping(GET_ROOMS_LIST)
    public ResponseEntity<List<RoomDTO>> getRoomsList() {
        return ResponseEntity.ok(roomBookingService.getRoomsList());
    }

    //retrieve meeting room's bookings with room id and date as search params, default ordering start time asc
    @PostMapping(ROOM_BOOKING_LIST)
    public ResponseEntity<PageDTO<RoomBookingDTO>> getRoomBookingsByCriteria(@RequestBody RoomBookingSearchDTO roomBookingSearchDTO) {
        return ResponseEntity.ok(roomBookingService.getRoomBookingsByCriteria(roomBookingSearchDTO));
    }
}

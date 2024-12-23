package com.acme.rbs.service.roombooking;

import com.acme.rbs.dto.request.RoomBookingSearchDTO;
import com.acme.rbs.dto.response.RoomBookingDTO;
import com.acme.rbs.dto.response.RoomDTO;
import com.acme.rbs.dto.response.pagination.PageDTO;

import java.util.List;

public interface RoomBookingService {

    List<RoomDTO> getRoomsList();

    PageDTO<RoomBookingDTO> getRoomBookingsByCriteria(RoomBookingSearchDTO roomBookingSearchDTO);

}

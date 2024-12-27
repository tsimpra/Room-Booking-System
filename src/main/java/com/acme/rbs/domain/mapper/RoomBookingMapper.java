package com.acme.rbs.domain.mapper;

import com.acme.rbs.domain.AcmeUser;
import com.acme.rbs.domain.Room;
import com.acme.rbs.domain.RoomBooking;
import com.acme.rbs.dto.request.BookingRequest;
import com.acme.rbs.dto.response.RoomBookingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomBookingMapper {
    @Mapping(target = "userMail", source = "acmeUser.email")
    RoomBookingDTO toDto(RoomBooking roomBooking);

    @Mapping(target = "room", source = "roomId", qualifiedByName = "populateRoom")
    @Mapping(target = "acmeUser", source = "userId", qualifiedByName = "populateAcmeUser")
    RoomBooking fromRequest(BookingRequest bookingRequest);

    @Named("populateRoom")
    default Room populateRoom(Long roomId) {
        return new Room(roomId, null, null, null);
    }

    @Named("populateAcmeUser")
    default AcmeUser populateAcmeUser(Long userId) {
        return new AcmeUser(userId, null, null, null, null, null);
    }
}
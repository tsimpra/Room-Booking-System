package com.acme.rbs.domain.mapper;

import com.acme.rbs.domain.RoomBooking;
import com.acme.rbs.dto.response.RoomBookingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomBookingMapper {
    @Mapping(target = "userMail", source = "acmeUser.email")
    RoomBookingDTO toDto(RoomBooking roomBooking);
}
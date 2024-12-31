package com.acme.rbs.domain.mapper;

import com.acme.rbs.domain.Room;
import com.acme.rbs.dto.response.RoomDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoomMapper {
    RoomDTO toDto(Room room);
}
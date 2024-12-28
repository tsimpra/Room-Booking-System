package com.acme.rbs.domain.mapper;

import com.acme.rbs.domain.AcmeUser;
import com.acme.rbs.domain.Room;
import com.acme.rbs.domain.RoomBooking;
import com.acme.rbs.dto.request.BookingRequest;
import com.acme.rbs.dto.response.RoomBookingDTO;
import com.acme.rbs.exception.BookingRequestException;
import com.acme.rbs.repository.AcmeUserRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class RoomBookingMapper {
    @Autowired
    private AcmeUserRepository acmeUserRepository;

    @Mapping(target = "userMail", source = "acmeUser.email")
    @Mapping(target = "userId", source = "acmeUser.id")
    public abstract RoomBookingDTO toDto(RoomBooking roomBooking);

    @Mapping(target = "room", source = "roomId", qualifiedByName = "populateRoom")
    @Mapping(target = "acmeUser", source = "userId", qualifiedByName = "populateAcmeUser")
    public abstract RoomBooking fromRequest(BookingRequest bookingRequest);

    @Named("populateRoom")
    protected Room populateRoom(Long roomId) {
        return new Room(roomId);
    }

    @Named("populateAcmeUser")
    protected AcmeUser populateAcmeUser(Long userId) {
        return acmeUserRepository.findById(userId)
                //we have already validated user existence in ValidationService,
                //so we can call get() directly but I prefer to throw an exception just in case
                .orElseThrow(() -> new BookingRequestException("Could not find user with id:" + userId));
        //Also since we have already fetched user in ValidationService, it would exist in Hibernate cache so it will not query database again
    }
}
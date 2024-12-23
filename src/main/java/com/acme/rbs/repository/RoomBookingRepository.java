package com.acme.rbs.repository;

import com.acme.rbs.domain.RoomBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {

    Page<RoomBooking> findByBookingDateAndRoomName(LocalDate bookingDate, String roomName, Pageable pageable);


}
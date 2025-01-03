package com.acme.rbs.repository;

import com.acme.rbs.domain.RoomBooking;
import com.acme.rbs.dto.response.RoomBookingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;

@Repository
public interface RoomBookingRepository extends JpaRepository<RoomBooking, Long> {

    @Query("select new com.acme.rbs.dto.response.RoomBookingDTO(rbk.id, rbk.bookingDate, rbk.startTime, rbk.endTime, aus.id, aus.email) " +
            "from RoomBooking rbk join AcmeUser aus on rbk.acmeUser.id = aus.id " +
            "and rbk.bookingDate = :bookingDate and rbk.room.id = :roomId")
    Page<RoomBookingDTO> findByBookingDateAndRoomId(LocalDate bookingDate, Long roomId, Pageable pageable);

    @Query("select case when count(rb.id) > 0 then true else false end from RoomBooking rb where " +
            "rb.startTime< :endTime and rb.endTime> :startTime " +
            "and rb.bookingDate= :bookingDate and rb.room.id = :roomId")
    boolean existsByStartTimeIsBeforeAndEndTimeIsAfterAndBookingDateAndRoomId(LocalTime endTime, LocalTime startTime, LocalDate bookingDate, Long roomId);
}
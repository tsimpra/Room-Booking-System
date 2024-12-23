package com.acme.rbs.repository;

import com.acme.rbs.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("select (count(r) > 0) from Room r where r.name = ?1")
    boolean existsByName(String name);
}
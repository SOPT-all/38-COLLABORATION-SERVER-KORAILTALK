package org.sopt.korailtalk.reservation.repository;

import org.sopt.korailtalk.reservation.domain.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    List<ReservationSeat> findAllByScheduleId(Long scheduleId);

    @Query("""
        select rs.seatNumber
        from ReservationSeat rs
        where rs.schedule.id = :scheduleId
    """)
    List<Integer> findReservedSeatNumbersByScheduleId(@Param("scheduleId")Long scheduleId);
}

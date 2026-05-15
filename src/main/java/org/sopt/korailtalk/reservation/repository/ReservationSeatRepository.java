package org.sopt.korailtalk.reservation.repository;

import org.sopt.korailtalk.reservation.domain.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {

    List<ReservationSeat> findAllByScheduleId(Long scheduleId);
}

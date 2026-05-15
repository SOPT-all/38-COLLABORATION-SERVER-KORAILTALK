package org.sopt.korailtalk.schedule.service;

import lombok.RequiredArgsConstructor;
import org.sopt.korailtalk.global.exception.BusinessException;
import org.sopt.korailtalk.global.exception.ErrorCode;
import org.sopt.korailtalk.reservation.repository.ReservationSeatRepository;
import org.sopt.korailtalk.schedule.domain.Schedule;
import org.sopt.korailtalk.schedule.dto.response.SeatListResponse;
import org.sopt.korailtalk.schedule.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {

    private final ScheduleRepository scheduleRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    public SeatListResponse getSeats(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        List<Integer> reservedSeats = reservationSeatRepository
                .findReservedSeatNumbersByScheduleId(schedule.getId());

        List<Integer> outletSeats = parseOutletSeatNumbers(schedule.getTrainType().getOutletSeatNumbers());

        return new SeatListResponse(
                schedule.getId(),
                reservedSeats,
                outletSeats
        );
    }

    private List<Integer> parseOutletSeatNumbers(String outletSeatNumbers) {
        if (outletSeatNumbers == null || outletSeatNumbers.isBlank()) {
            return List.of();
        }
        return Arrays.stream(outletSeatNumbers.split(","))
                .map(String::trim)
                // 데이터에 마지막 쉼표나 연속 쉼표가 있어도 빈 문자열은 좌석 번호로 보지 않는다.
                .filter(seatNumber -> !seatNumber.isBlank())
                .map(Integer::valueOf)
                .toList();
    }
}

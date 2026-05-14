package org.sopt.korailtalk.schedule.service;

import lombok.RequiredArgsConstructor;
import org.sopt.korailtalk.global.exception.BusinessException;
import org.sopt.korailtalk.global.exception.ErrorCode;
import org.sopt.korailtalk.reservation.domain.ReservationSeat;
import org.sopt.korailtalk.reservation.repository.ReservationSeatRepository;
import org.sopt.korailtalk.schedule.domain.Schedule;
import org.sopt.korailtalk.schedule.domain.TrainType;
import org.sopt.korailtalk.schedule.dto.response.ScheduleListResponse;
import org.sopt.korailtalk.schedule.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    // 최대 좌석 수
    private static final int MAX_SEAT_COUNT = 32;

    private final ScheduleRepository scheduleRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    // 열차 일정 목록 조회
    public ScheduleListResponse getSchedules() {
        List<Schedule> schedules = scheduleRepository.findAllByOrderByDepartureTimeAsc();

        if (schedules.isEmpty()) {
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND);
        }

        return new ScheduleListResponse(
                schedules.stream()
                        .map(this::toScheduleResponse)
                        .toList()
        );
    }

    // Schedule 엔티티를 ScheduleResponse DTO로 변환하는 메서드,
    private ScheduleListResponse.ScheduleResponse toScheduleResponse(Schedule schedule) {

        TrainType trainType = schedule.getTrainType();

        // 해당 일정에 예약된 좌석 번호를 조회하여 Set<Integer>로 변환한다. 예약된 좌석 번호는 문자열로 저장되어 있으므로 Integer로 변환한다.
        Set<Integer> reservedSeats = reservationSeatRepository.findAllByScheduleId(schedule.getId())
                .stream()
                .map(ReservationSeat::getSeatNumber)
                .map(Integer::valueOf)
                .collect(Collectors.toSet());

        // 열차 유형의 outletSeatNumbers 필드를 파싱하여 List<Integer>로 변환한다. outletSeatNumbers는 문자열로 저장되어 있으므로 Integer로 변환한다.
        List<Integer> outletSeats = parseOutletSeatNumbers(trainType.getOutletSeatNumbers());

        return new ScheduleListResponse.ScheduleResponse(
                schedule.getId(),
                trainType.getTrainName(),
                schedule.getTrainName(),
                schedule.getDepartureTime(),
                schedule.getArrivalTime(),
                trainType.getGeneralPrice(),
                trainType.getSpecialPrice(),
                reservedSeats.size() >= MAX_SEAT_COUNT,
                !outletSeats.isEmpty() && reservedSeats.containsAll(outletSeats)
        );
    }

    // 열차 유형의 outletSeatNumbers 필드를 파싱하여 List<Integer>로 반환하는 메서드
    private List<Integer> parseOutletSeatNumbers(String outletSeatNumbers) {
        if (outletSeatNumbers == null || outletSeatNumbers.isBlank()) {
            return List.of();
        }

        return Arrays.stream(outletSeatNumbers.split(","))
                .map(String::trim)
                .filter(seatNumber -> !seatNumber.isBlank())
                .map(Integer::valueOf)
                .toList();
    }
}

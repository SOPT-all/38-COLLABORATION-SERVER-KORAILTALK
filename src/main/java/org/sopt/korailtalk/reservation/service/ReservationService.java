package org.sopt.korailtalk.reservation.service;

import lombok.RequiredArgsConstructor;
import org.sopt.korailtalk.global.exception.BusinessException;
import org.sopt.korailtalk.global.exception.ErrorCode;
import org.sopt.korailtalk.reservation.domain.ReservationSeat;
import org.sopt.korailtalk.reservation.dto.request.ReservationCreateRequest;
import org.sopt.korailtalk.reservation.dto.response.ReservationCreateResponse;
import org.sopt.korailtalk.reservation.dto.response.ReservationHistoryListResponse;
import org.sopt.korailtalk.reservation.dto.response.ReservationHistoryResponse;
import org.sopt.korailtalk.reservation.repository.ReservationSeatRepository;
import org.sopt.korailtalk.schedule.domain.Schedule;
import org.sopt.korailtalk.schedule.repository.ScheduleRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ScheduleRepository scheduleRepository;
    private final ReservationSeatRepository reservationSeatRepository;

    @Transactional
    public ReservationCreateResponse createReservation(ReservationCreateRequest request) {
        // 예약 요청이 들어온 스케줄이 실제로 존재하는지 확인한다.
        Schedule schedule = scheduleRepository.findById(request.scheduleId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND));

        // 혹시 좌석 순서가 바뀌어 들어와도 오름차순으로 응답하기 위해 정렬부터 한다.
        List<Integer> sortedSeatNumbers = request.seatNumbers()
                .stream()
                .sorted()
                .toList();

        validateSeatAvailability(schedule.getId(), sortedSeatNumbers);

        // 저장할 예약의 리스트를 만든다.
        List<ReservationSeat> reservationSeats = sortedSeatNumbers.stream()
                .map(seatNumber -> new ReservationSeat(schedule, request.userId(), seatNumber))
                .toList();

        try {
            reservationSeatRepository.saveAll(reservationSeats);
            // 동시에 같은 좌석을 예약하는 경우 DB UNIQUE 제약에서 최종적으로 한 번 더 막는다.
            reservationSeatRepository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.SEAT_ALREADY_RESERVED);
        }

        return new ReservationCreateResponse(schedule.getId(), request.userId(), sortedSeatNumbers);
    }

    // 좌석 검증
    private void validateSeatAvailability(Long scheduleId, List<Integer> seatNumbers) {
        Set<Integer> reservedSeatNumbers = new HashSet<>(
                reservationSeatRepository.findReservedSeatNumbersByScheduleId(scheduleId)
        );

        // 요청 좌석 중 이미 예약된 좌석이 하나라도 있으면 전체 예약을 실패시킨다.
        boolean hasAlreadyReservedSeat = seatNumbers.stream()
                .anyMatch(reservedSeatNumbers::contains);

        if (hasAlreadyReservedSeat) {
            throw new BusinessException(ErrorCode.SEAT_ALREADY_RESERVED);
        }
    }

    @Transactional(readOnly = true)
    public ReservationHistoryListResponse getReservationHistory(Long userId) {
        List<ReservationSeat> reservationSeats = reservationSeatRepository.findReservationSeatsByUserId(userId);

        Map<Long, List<ReservationSeat>> groupedReservations = reservationSeats
                .stream()
                .collect(Collectors.groupingBy(
                        reservationSeat -> reservationSeat.getSchedule().getId()
                ));

        List<ReservationHistoryResponse> reservations = new ArrayList<>();

        for (List<ReservationSeat> reservationGroup : groupedReservations.values()) {
            ReservationSeat firstReservation = reservationGroup.getFirst();
            Schedule schedule = firstReservation.getSchedule();

            List<Integer> seatNumbers = reservationGroup.stream()
                    .map(ReservationSeat::getSeatNumber)
                    .sorted()
                    .toList();

            int totalPrice = seatNumbers.size() * schedule.getTrainType().getGeneralPrice();

            ReservationHistoryResponse response = new ReservationHistoryResponse(
                    schedule.getId(),
                    schedule.getTrainType().getTrainName(),
                    schedule.getTrainType().getTrainName(),
                    schedule.getDepartureTime(),
                    schedule.getArrivalTime(),
                    seatNumbers,
                    totalPrice
            );

            reservations.add(response);
        }

        return new ReservationHistoryListResponse(reservations);
    }
}

package org.sopt.korailtalk.reservation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationHistoryResponse (
        Long scheduleId,
        String trainType,
        String trainName,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        List<Integer> seatNumbers,
        int totalPrice
){
}

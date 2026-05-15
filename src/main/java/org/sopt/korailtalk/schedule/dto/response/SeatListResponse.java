package org.sopt.korailtalk.schedule.dto.response;

import java.util.List;

public record SeatListResponse(
        Long scheduleId,
        List<Integer> reservedSeats,
        List<Integer> outletSeats
) {
}

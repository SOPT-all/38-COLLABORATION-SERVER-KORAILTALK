package org.sopt.korailtalk.reservation.dto.response;

import java.util.List;

public record ReservationHistoryListResponse (
        List<ReservationHistoryResponse> reservations
){
}

package org.sopt.korailtalk.reservation.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.korailtalk.global.domain.BaseTimeEntity;
import org.sopt.korailtalk.schedule.domain.Schedule;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "reservation_seats",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reservation_seat_schedule_id_seat_number",
                        columnNames = {"schedule_id", "seat_number"}
                )
        }
)
public class ReservationSeat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "seat_number", nullable = false, length = 20)
    private String seatNumber;

    public ReservationSeat(
            Schedule schedule,
            Long userId,
            String seatNumber
    ) {
        this.schedule = schedule;
        this.userId = userId;
        this.seatNumber = seatNumber;
    }
}

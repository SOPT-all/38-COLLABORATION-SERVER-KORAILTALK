package org.sopt.korailtalk.schedule.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.korailtalk.global.domain.BaseTimeEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedules")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_type_id", nullable = false)
    private TrainType trainType;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    public Schedule(
            TrainType trainType,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime

    ) {
        this.trainType = trainType;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }
}

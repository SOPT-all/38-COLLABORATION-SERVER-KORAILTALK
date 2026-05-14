package org.sopt.korailtalk.schedule.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "train_types")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "train_name", nullable = false, length = 100)
    private String trainName;

    @Column(name = "general_price", nullable = false)
    private Integer generalPrice;

    @Column(name = "special_price", nullable = false)
    private Integer specialPrice;

    @Column(name = "outlet_seat_numbers", length = 100)
    private String outletSeatNumbers;

    public TrainType(
            String trainName,
            Integer generalPrice,
            Integer specialPrice,
            String outletSeatNumbers
    ) {
        this.trainName = trainName;
        this.generalPrice = generalPrice;
        this.specialPrice = specialPrice;
        this.outletSeatNumbers = outletSeatNumbers;
    }
}
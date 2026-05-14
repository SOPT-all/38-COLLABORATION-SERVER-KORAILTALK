-- KorailTalk 데모 DB schema입니다.
--
-- reset-local-db-to-seed.sh, reset-demo-db-to-seed.sh가 이 파일을 먼저 실행한 뒤
-- data.sql을 실행합니다. prod 프로필은 ddl-auto=validate라서 원격 DB에 이 schema가
-- 먼저 만들어져 있어야 애플리케이션이 정상 부팅됩니다.

CREATE TABLE IF NOT EXISTS train_types (
    id BIGINT NOT NULL AUTO_INCREMENT,
    train_name VARCHAR(100) NOT NULL,
    general_price INT NOT NULL,
    special_price INT NOT NULL,
    outlet_seat_numbers VARCHAR(100),
    PRIMARY KEY (id),
    CONSTRAINT uk_train_types_train_name UNIQUE (train_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS schedules (
    id BIGINT NOT NULL AUTO_INCREMENT,
    train_type_id BIGINT NOT NULL,
    train_name VARCHAR(100) NOT NULL,
    departure_time DATETIME(6) NOT NULL,
    arrival_time DATETIME(6) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_schedules_train_type_id
        FOREIGN KEY (train_type_id) REFERENCES train_types (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS reservation_seats (
    id BIGINT NOT NULL AUTO_INCREMENT,
    schedule_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    seat_number VARCHAR(20) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_reservation_seat_schedule_id_seat_number
        UNIQUE (schedule_id, seat_number),
    CONSTRAINT fk_reservation_seats_schedule_id
        FOREIGN KEY (schedule_id) REFERENCES schedules (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

# 듀레일 검증 기록

## 기준 판본

| 구분 | 확인 결과 |
| --- | --- |
| 팀 서버 리모트 기본 브랜치 | [`28e2567`](https://github.com/SOPT-all/38-COLLABORATION-SERVER-KORAILTALK/commit/28e2567f0aa33ee9fc8f10e57f3bf773cb27d77b), 시드 시나리오 [PR #31](https://github.com/SOPT-all/38-COLLABORATION-SERVER-KORAILTALK/pull/31) 병합 |
| 로컬 데모 시드 브랜치 | [`f7a1767`](https://github.com/SOPT-all/38-COLLABORATION-SERVER-KORAILTALK/commit/f7a1767b5f88f4f4043e2f02924daceb6c754a4e), 현재 원격 main과 Git 트리 `06b2e892ce432f5dd7aa572704fa6a06052771dc`가 동일 |
| 로컬 main 캐시 | `6c8580b`; 예약 API까지 반영된 이전 판본으로, 최신 원격 main과 동일하지 않음 |
| 팀 iOS | [팀 iOS 저장소](https://github.com/SOPT-all/38-COLLABORATION-iOS-KORAILTALK) develop `e170e89`; 시연은 프로젝트 README에 게시된 팀 기록이며 촬영 커밋은 명시되지 않음 |

팀 기획 문서는 제품을 고른 이유와 콘센트 위치 표기의 근거를 확인하는 데, 프로젝트 범위가 일치하는 초기 개발 대화는 공통 규약·API·시드 결정의 배경을 확인하는 데 사용했습니다. 담당 일은 Git 커밋과 PR 구현부를 다시 확인했습니다. 기획에서 검토한 대안과 실제 코드에 반영된 내용을 구분했습니다.

기획 단계에서는 이용 후기·콘텐츠 등 온라인 자료를 조사했습니다. 인터뷰가 실시됐다고 주장하지 않습니다. 작성자 구현은 일정 조회([PR #18](https://github.com/SOPT-all/38-COLLABORATION-SERVER-KORAILTALK/pull/18)), 예약 생성([PR #24](https://github.com/SOPT-all/38-COLLABORATION-SERVER-KORAILTALK/pull/24)), 좌석 조회 병합 조정([PR #25](https://github.com/SOPT-all/38-COLLABORATION-SERVER-KORAILTALK/pull/25)), 공통 예외([PR #5](https://github.com/SOPT-all/38-COLLABORATION-SERVER-KORAILTALK/pull/5))와 시드·배포 개선입니다. 내역 API([PR #27](https://github.com/SOPT-all/38-COLLABORATION-SERVER-KORAILTALK/pull/27))와 iOS 화면은 팀 작업입니다.

## 시연 흐름과 범위

| 역할 · 목적 | 시작 → 조작 → 결과 | 자료 |
| --- | --- | --- |
| 여행자 · 콘센트 자리 찾기 | 승차권 목록 → 열차 고르기 → 콘센트와 예약된 자리 확인 → 승차권 결과 | 팀 iOS README의 네 편. 서로 다른 팀 시연을 한 세션으로 합치지 않음 |
| 서버 연동 개발자 · 예약 결과 확인 | Swagger의 일정·좌석 조회 → 비어 있는 콘센트 좌석 3번 요청 → 신규 내역에서 좌석/총액 확인 | 원본 Swagger UI 한 세션을 녹화한 GIF, 실제 Spring Boot와 MySQL 연결 |
| 서버 연동 개발자 · 중복 좌석 처리 확인 | 3번 예약 뒤 같은 열차의 3·4번 같이 요청 → 충돌 409 → 4번은 여전히 빈 자리 | 아래 HTTP/상태 검증. 순차 요청 시험이며 동시성 부하 시험은 아님 |

iOS 화면은 팀 README에 공개된 네 편의 시연입니다. 각 편은 별개이며, 화면의 결제 완료는 팀 앱의 시연 장면입니다.

## 서버 로컬 실행

- Java 21, Gradle 9.4.1, Spring Boot 4.0.6. 팀 소스의 `bootJar` 빌드 성공.
- 별도 MySQL 9.2.0 임시 인스턴스에 커밋의 `schema.sql`, `data.sql`을 UTF-8로 적용했습니다. 원본 코드를 수정하지 않고 Spring 데이터베이스·리스닝 주소만 실행 옵션으로 지정했습니다. 서버·DB는 루프백에만 열었습니다.
- 일정·좌석 조회와 좌석 3번 예약·내역 확인은 SpringDoc Swagger UI에서 실행했습니다. 3·4번 혼합 요청의 409와 후속 상태는 curl로 별도 확인했습니다. 4242, 4243은 별도 임시 DB의 테스트 식별자입니다. 운영 서버, 팀 공유 DB 및 실제 예매·결제에는 접근하지 않았습니다.

| 검증 | 실제 결과 |
| --- | --- |
| `GET /health` | 200, `UP` |
| `GET /api/v1/schedules` | 200, 일정 1·2를 포함한 시간순 목록. 2번의 `isOutletSoldOut=true` |
| `GET /api/v1/schedules/1/seats` | 200, 3번이 `outletSeats`에 있고 예약 목록에는 없음 |
| `POST /api/v1/reservations` | 201, 일정 1·사용자 4242·`seatNumbers: [3]` |
| `GET /api/v1/users/4242/reservations` | 200, `KTX 001`의 예약 좌석 3번과 총액 59,800원 |
| `POST /api/v1/reservations`로 사용자 4243이 3·4번 요청 | 409, `SEAT_002`. 해당 사용자의 내역은 빈 배열이고 좌석 조회의 예약 목록에는 4번이 추가되지 않음 |

배포 그림은 저장소에 남은 팀 데모 구성, 즉 GitHub Actions → ARM64 이미지 → EC2 Docker → RDS 및 별도 수동 DB 초기화의 근거를 나타냅니다. 공개 운영 주소·가동 상태를 새로 추정하지 않았습니다. 도식은 실제 연결된 노드·엣지의 편집용 `.drawio`이며 diagrams.net 공식 뷰어로 렌더한 PNG를 함께 제공합니다.

## 영상·문서 검사

- Swagger 영상은 브라우저 화면 녹화 841프레임을 원래 타임스탬프로 25fps에 맞췄습니다. 이동 중 소스 프레임 간격 중앙값 16.65ms, 90백분위 24.66ms. 새 동작을 만들거나 프레임 보간하지 않았습니다.
- 읽기용 정지 구간과 진행 바를 넣기 전 편집본은 940×710, **849프레임 / 33.96초 = 25.00fps**. 디코딩한 5.5초(일정), 14.3초(좌석), 20.5초(예약 요청), 23.3초(예약 결과), 32.2초(나의 예매 내역)를 확인했습니다. 화면 폭에서 KTX 001, 201/200, 좌석 3번 및 59,800원 필드를 읽을 수 있습니다.
- 아키텍처 3종을 실제 공식 뷰어로 렌더했습니다. 편집 원본 안에 각 노드와 엣지가 있으며 이미지 한 장을 넣어 만든 소스가 아닙니다.
- 원래 로컬 JAR 의존성만 사용할 때는 정확한 Gradle 배포물과 Spring Boot 아티팩트가 캐시에 없었습니다. 격리 복사본 빌드에 공개 고정 버전 의존성을 받아 해결했습니다. iOS 재촬영은 설치된 실행 환경의 제약으로 미완료입니다.

## README 재생용 편집

최종 README GIF는 940×720, 37.24초 / 931프레임입니다. 콘센트 좌석 응답과 예약·내역 결과를 보는 시간을 늘렸습니다. 전체는 25fps이며 화면 전환의 원래 속도는 유지했다. 편집으로 추가한 정지는 서비스 지연 시간이 아니다. GIF 아래 10px 여백에 재생 진행 바를 넣었으며 앱 화면은 가리지 않는다.

팀 iOS 영상은 각자 독립된 시연입니다.

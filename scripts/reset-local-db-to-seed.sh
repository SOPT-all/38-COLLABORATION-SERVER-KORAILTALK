#!/usr/bin/env bash

# 로컬 Docker MySQL을 seed 상태로 초기화하는 스크립트입니다.
#
# 사용법:
#   bash scripts/reset-local-db-to-seed.sh
#
# 실행 전 준비:
#   1. 로컬 MySQL 실행: docker compose up -d mysql
#   2. Spring 애플리케이션을 한 번 실행해서 JPA/Hibernate가 테이블을 만들게 하기
#
# 동작 방식:
#   1. korailtalk-mysql 컨테이너가 꺼져 있으면 docker compose로 실행합니다.
#   2. reservation_seats -> schedules -> train_types 순서로 기존 데이터를 삭제합니다.
#   3. src/main/resources/data.sql을 다시 실행해 Google Sheet 기준 seed를 넣습니다.
#
# 주의:
#   - 로컬 개발 DB 전용입니다.
#   - 테이블이 아직 없다면 실패합니다. 이 경우 Spring 애플리케이션을 한 번 실행한 뒤 다시 시도하세요.

set -euo pipefail

CONTAINER_NAME="korailtalk-mysql"
DATABASE_NAME="korailtalk"
DATABASE_USER="korail"
DATABASE_PASSWORD="korail1234"
SEED_FILE="src/main/resources/data.sql"

if [ ! -f "$SEED_FILE" ]; then
  echo "Seed 파일을 찾을 수 없습니다: $SEED_FILE"
  echo "프로젝트 루트에서 이 스크립트를 실행해주세요."
  exit 1
fi

if ! docker ps --format '{{.Names}}' | grep -qx "$CONTAINER_NAME"; then
  echo "로컬 MySQL 컨테이너가 실행 중이 아닙니다."
  echo "다음 명령어로 MySQL을 실행합니다: docker compose up -d mysql"
  docker compose up -d mysql
fi

echo "로컬 MySQL이 연결 가능한 상태가 될 때까지 기다립니다..."
for _ in {1..30}; do
  if docker exec "$CONTAINER_NAME" mysqladmin ping -u"$DATABASE_USER" -p"$DATABASE_PASSWORD" --silent >/dev/null 2>&1; then
    break
  fi
  sleep 1
done

if ! docker exec "$CONTAINER_NAME" mysqladmin ping -u"$DATABASE_USER" -p"$DATABASE_PASSWORD" --silent >/dev/null 2>&1; then
  echo "제한 시간 안에 MySQL이 준비되지 않았습니다."
  exit 1
fi

echo "기존 로컬 더미데이터를 삭제합니다..."
if ! docker exec -i "$CONTAINER_NAME" mysql -u"$DATABASE_USER" -p"$DATABASE_PASSWORD" "$DATABASE_NAME" <<'SQL'; then
DELETE FROM reservation_seats;
DELETE FROM schedules;
DELETE FROM train_types;
ALTER TABLE reservation_seats AUTO_INCREMENT = 1;
ALTER TABLE schedules AUTO_INCREMENT = 1;
ALTER TABLE train_types AUTO_INCREMENT = 1;
SQL
  echo
  echo "기존 데이터 삭제에 실패했습니다."
  echo "가장 흔한 원인은 아직 테이블이 만들어지지 않은 경우입니다."
  echo "local 프로필로 Spring 애플리케이션을 한 번 실행한 뒤 다시 시도해주세요."
  exit 1
fi

echo "$SEED_FILE 파일의 seed 데이터를 다시 적용합니다..."
if ! docker exec -i "$CONTAINER_NAME" mysql -u"$DATABASE_USER" -p"$DATABASE_PASSWORD" "$DATABASE_NAME" < "$SEED_FILE"; then
  echo
  echo "Seed 적용에 실패했습니다."
  echo "테이블 구조가 data.sql의 컬럼과 일치하는지 확인해주세요."
  exit 1
fi

echo "로컬 DB가 seed 상태로 초기화되었습니다."

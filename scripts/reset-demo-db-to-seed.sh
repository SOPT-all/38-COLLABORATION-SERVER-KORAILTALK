#!/usr/bin/env bash

# 원격 데모 DB를 seed 상태로 초기화하는 스크립트입니다.
#
# GitHub Actions에서 실행할 때는 GitHub Secrets의 값을 환경변수로 전달합니다.
#
# 필요한 환경변수:
#   CONFIRM_RESET=YES
#   DB_URL=jdbc:mysql://호스트:포트/DB이름
#   DB_USERNAME=DB 사용자명
#   DB_PASSWORD=DB 비밀번호
#
# 로컬에서 직접 실행할 때도 같은 환경변수를 넘기면 됩니다.
#
# 동작 방식:
#   1. CONFIRM_RESET=YES가 있는지 확인해서 실수 실행을 막습니다.
#   2. DB_URL에서 host, port, database 이름을 파싱합니다.
#   3. reservation_seats -> schedules -> train_types 순서로 기존 데이터를 삭제합니다.
#   4. src/main/resources/data.sql을 다시 실행해 Google Sheet 기준 seed를 넣습니다.
#
# 주의:
#   - 데모 DB 전용입니다.
#   - 실제 운영 데이터가 들어 있는 DB에는 실행하면 안 됩니다.
#   - 이 스크립트는 mysql CLI가 설치된 환경에서 실행됩니다.

set -euo pipefail

SEED_FILE="src/main/resources/data.sql"

if [ "${CONFIRM_RESET:-}" != "YES" ]; then
  echo "원격 DB 초기화는 파괴적인 작업입니다."
  echo "실행하려면 CONFIRM_RESET=YES 환경변수를 설정해주세요."
  exit 1
fi

if [ ! -f "$SEED_FILE" ]; then
  echo "Seed 파일을 찾을 수 없습니다: $SEED_FILE"
  echo "프로젝트 루트에서 이 스크립트를 실행해주세요."
  exit 1
fi

if ! command -v mysql >/dev/null 2>&1; then
  echo "mysql CLI를 찾을 수 없습니다."
  echo "GitHub Actions에서는 workflow가 mysql-client를 설치한 뒤 이 스크립트를 실행합니다."
  exit 1
fi

DB_URL="${DB_URL:?DB_URL 환경변수가 필요합니다.}"
DB_USERNAME="${DB_USERNAME:?DB_USERNAME 환경변수가 필요합니다.}"
DB_PASSWORD="${DB_PASSWORD:?DB_PASSWORD 환경변수가 필요합니다.}"

JDBC_URL="${DB_URL#jdbc:mysql://}"
JDBC_URL="${JDBC_URL%%\?*}"
DB_HOST_PORT="${JDBC_URL%%/*}"
DB_NAME="${JDBC_URL#*/}"
DB_HOST="${DB_HOST_PORT%%:*}"
DB_PORT="3306"

if [ "$DB_HOST_PORT" != "$DB_HOST" ]; then
  DB_PORT="${DB_HOST_PORT#*:}"
fi

if [ -z "$DB_HOST" ] || [ -z "$DB_NAME" ] || [ "$DB_NAME" = "$JDBC_URL" ]; then
  echo "DB_URL을 파싱할 수 없습니다: $DB_URL"
  echo "예상 형식: jdbc:mysql://호스트:포트/DB이름"
  exit 1
fi

run_mysql() {
  MYSQL_PWD="$DB_PASSWORD" mysql \
    --protocol=TCP \
    --host="$DB_HOST" \
    --port="$DB_PORT" \
    --user="$DB_USERNAME" \
    "$DB_NAME"
}

echo "원격 데모 DB를 seed 상태로 초기화합니다."
echo "대상 DB: $DB_HOST:$DB_PORT/$DB_NAME"
echo "기존 데모 데이터를 삭제합니다..."

if ! run_mysql <<'SQL'; then
DELETE FROM reservation_seats;
DELETE FROM schedules;
DELETE FROM train_types;
ALTER TABLE reservation_seats AUTO_INCREMENT = 1;
ALTER TABLE schedules AUTO_INCREMENT = 1;
ALTER TABLE train_types AUTO_INCREMENT = 1;
SQL
  echo
  echo "기존 데이터 삭제에 실패했습니다."
  echo "테이블이 존재하는지, DB 계정에 DELETE/ALTER 권한이 있는지 확인해주세요."
  exit 1
fi

echo "$SEED_FILE 파일의 seed 데이터를 다시 적용합니다..."
if ! run_mysql < "$SEED_FILE"; then
  echo
  echo "Seed 적용에 실패했습니다."
  echo "테이블 구조가 data.sql의 컬럼과 일치하는지 확인해주세요."
  exit 1
fi

echo "원격 데모 DB가 seed 상태로 초기화되었습니다."

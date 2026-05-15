#!/usr/bin/env bash

# 원격 데모 DB를 seed 상태로 초기화하는 스크립트입니다.
#
# GitHub Actions에서 실행할 때는 EC2에 접속한 뒤 GitHub Secrets의 값을
# 환경변수로 전달합니다.
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
#   3. 기존 데모 테이블을 삭제합니다.
#   4. src/main/resources/schema.sql로 테이블을 다시 만듭니다.
#   5. src/main/resources/data.sql을 다시 실행해 Google Sheet 기준 seed를 넣습니다.
#
# 주의:
#   - 데모 DB 전용입니다.
#   - 실제 운영 데이터가 들어 있는 DB에는 실행하면 안 됩니다.
#   - 이 스크립트는 mysql CLI가 설치된 환경에서 실행됩니다.
#   - workflow는 EC2에 mysql CLI가 없으면 Docker mysql 클라이언트로 실행합니다.

set -euo pipefail

SCHEMA_FILE="src/main/resources/schema.sql"
SEED_FILE="src/main/resources/data.sql"

if [ "${CONFIRM_RESET:-}" != "YES" ]; then
  echo "원격 DB 초기화는 파괴적인 작업입니다."
  echo "실행하려면 CONFIRM_RESET=YES 환경변수를 설정해주세요."
  exit 1
fi

if [ ! -f "$SCHEMA_FILE" ]; then
  echo "Schema 파일을 찾을 수 없습니다: $SCHEMA_FILE"
  echo "프로젝트 루트에서 이 스크립트를 실행해주세요."
  exit 1
fi

if [ ! -f "$SEED_FILE" ]; then
  echo "Seed 파일을 찾을 수 없습니다: $SEED_FILE"
  echo "프로젝트 루트에서 이 스크립트를 실행해주세요."
  exit 1
fi

if ! command -v mysql >/dev/null 2>&1; then
  echo "mysql CLI를 찾을 수 없습니다."
  echo "EC2에 mysql CLI를 설치하거나 Docker mysql 클라이언트로 이 스크립트를 실행해주세요."
  exit 1
fi

DB_URL="${DB_URL:?DB_URL 환경변수가 필요합니다.}"
DB_USERNAME="${DB_USERNAME:?DB_USERNAME 환경변수가 필요합니다.}"
DB_PASSWORD="${DB_PASSWORD:?DB_PASSWORD 환경변수가 필요합니다.}"

strip_line_breaks() {
  local value="$1"
  value="${value//$'\r'/}"
  value="${value//$'\n'/}"
  printf '%s' "$value"
}

trim_secret() {
  local value
  value="$(strip_line_breaks "$1")"
  value="${value#"${value%%[![:space:]]*}"}"
  value="${value%"${value##*[![:space:]]}"}"
  printf '%s' "$value"
}

# GitHub Secrets에 값이 붙여넣기될 때 끝 줄바꿈이 섞이면 DB 이름에
# 그대로 포함될 수 있어 JDBC URL 파싱 전에 정리합니다.
DB_URL="$(trim_secret "$DB_URL")"
DB_USERNAME="$(trim_secret "$DB_USERNAME")"
DB_PASSWORD="$(strip_line_breaks "$DB_PASSWORD")"

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
    --default-character-set=utf8mb4 \
    --protocol=TCP \
    --host="$DB_HOST" \
    --port="$DB_PORT" \
    --connect-timeout=10 \
    --user="$DB_USERNAME" \
    "$DB_NAME"
}

echo "원격 데모 DB를 seed 상태로 초기화합니다."
echo "대상 DB: $DB_HOST:$DB_PORT/$DB_NAME"
echo "기존 데모 테이블을 삭제합니다..."

if ! run_mysql <<'SQL'; then
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS reservation_seats;
DROP TABLE IF EXISTS schedules;
DROP TABLE IF EXISTS train_types;
SET FOREIGN_KEY_CHECKS = 1;
SQL
  echo
  echo "기존 데모 테이블 삭제에 실패했습니다."
  echo "DB 연결 정보, 네트워크 접근, DB 계정의 DROP 권한을 확인해주세요."
  exit 1
fi

echo "$SCHEMA_FILE 파일의 schema를 다시 적용합니다..."
if ! run_mysql < "$SCHEMA_FILE"; then
  echo
  echo "Schema 적용에 실패했습니다."
  echo "DB 연결 정보, 네트워크 접근, DB 계정의 CREATE/ALTER 권한을 확인해주세요."
  exit 1
fi

echo "$SEED_FILE 파일의 seed 데이터를 다시 적용합니다..."
if ! run_mysql < "$SEED_FILE"; then
  echo
  echo "Seed 적용에 실패했습니다."
  echo "DB 연결 정보, 네트워크 접근, 테이블 구조와 data.sql 컬럼 일치 여부를 확인해주세요."
  exit 1
fi

echo "원격 데모 DB가 seed 상태로 초기화되었습니다."

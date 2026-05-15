# 38-COLLABORATION-SERVER-KORAILTALK
LET'S SOPT 38기 합동세미나 서버 코레일톡 🚅

## 로컬 더미데이터 초기화

로컬 MySQL을 Google Sheet 기준 seed 상태로 초기화할 때 사용합니다.

처음 세팅할 때는 MySQL을 띄운 뒤 reset 스크립트를 실행합니다.

```bash
docker compose up -d mysql
bash scripts/reset-local-db-to-seed.sh
```

이미 MySQL 컨테이너가 실행 중이라면 다음부터는 reset 스크립트만 실행하면 됩니다.

```bash
bash scripts/reset-local-db-to-seed.sh
```

Spring 애플리케이션 실행은 seed 적용 뒤에 하면 됩니다.

동작 방식:

- 기존 데모 테이블을 삭제합니다.
- `src/main/resources/schema.sql`을 실행해서 테이블을 다시 만듭니다.
- `src/main/resources/data.sql`을 실행해서 seed 데이터를 다시 넣습니다.
- 로컬 Docker MySQL 접속 정보는 `docker-compose.yml`에 공개된 개발용 값과 동일합니다.

## 원격 데모 DB 초기화

배포된 데모 DB를 같은 seed 상태로 되돌릴 때는 GitHub Actions의 `Reset Demo DB` workflow를 수동 실행합니다.

실행 방법:

1. GitHub 저장소의 `Actions` 탭으로 이동합니다.
2. `Reset Demo DB` workflow를 선택합니다.
3. `Run workflow`를 누릅니다.
4. 입력값에 `RESET`을 입력하고 실행합니다.

이 workflow는 GitHub Secrets에 있는 `EC2_HOST`, `EC2_USERNAME`, `EC2_SSH_KEY`로 EC2에 접속한 뒤, EC2에서 `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`를 사용해 원격 DB를 초기화합니다. 원격 DB 접속 정보는 코드에 하드코딩하지 않습니다.

주의할 점:

- 이 작업은 데모 DB 테이블을 삭제한 뒤 schema와 seed를 다시 넣습니다.
- 실제 운영 데이터가 들어 있는 DB에는 실행하면 안 됩니다.
- 원격 배포는 `prod` 프로필에서 `ddl-auto=validate`로 실행되므로, 새 엔티티가 추가된 뒤에는 이 workflow로 DB schema를 먼저 맞춰야 합니다.

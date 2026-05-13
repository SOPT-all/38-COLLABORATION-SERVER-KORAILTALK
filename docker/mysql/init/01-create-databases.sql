-- MySQL 컨테이너가 처음 만들어질 때 실행되는 초기화 SQL이다.
-- korailtalk: 로컬 앱 실행과 기획 더미데이터 확인에 사용한다.
-- korailtalk_test: 테스트가 데이터를 자유롭게 만들고 지울 수 있는 빈 DB이다.

CREATE DATABASE IF NOT EXISTS korailtalk_test;

GRANT ALL PRIVILEGES ON korailtalk_test.* TO 'korail'@'%';

FLUSH PRIVILEGES;

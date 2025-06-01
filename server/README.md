# TalesWiki Server

<div align="center">
  <img src="../client/public/og-image.png" alt="TalesWiki Logo" width="200"/>
  
  **테일즈위키 - 게임 커뮤니티 위키 서버**
  
  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
  [![Java](https://img.shields.io/badge/Java-21-007396?style=flat-square&logo=openjdk)](https://www.oracle.com/java/)
  [![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql)](https://www.mysql.com/)
  [![License](https://img.shields.io/badge/License-MIT-green.svg?style=flat-square)](LICENSE)
</div>

## 📋 목차

- [개요](#-개요)
- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [시작하기](#-시작하기)
- [프로젝트 구조](#-프로젝트-구조)
- [API 문서](#-api-문서)
- [데이터베이스 스키마](#-데이터베이스-스키마)
- [모니터링](#-모니터링)
- [테스트](#-테스트)
- [배포](#-배포)

## 🎮 개요

- TalesWiki 서버는 Spring Boot 3.4.5 기반의 RESTful API 서버로, 게임 커뮤니티를 위한 위키 시스템의 백엔드를 담당합니다.
- DDD(Domain-Driven Design) 아키텍처를 따르며, 실시간 통신, 검색, 이미지 처리 등 다양한 기능을 제공합니다.

### 주요 특징

- 🏗️ **클린 아키텍처** - 레이어드 아키텍처 & DDD 패턴
- 🔍 **고성능 검색** - Elasticsearch 기반 전문 검색
- 💬 **실시간 통신** - WebSocket을 통한 실시간 채팅
- 🖼️ **이미지 처리** - R2 저장소 & WebP 자동 변환
- 📊 **모니터링** - Prometheus + Grafana 통합
- 🔒 **보안** - IP 기반 차단 & 관리자 인증

## ✨ 주요 기능

### 1. 사전(Dictionary) 시스템
- **CRUD 작업**: 사전 항목 생성, 조회, 수정, 삭제
- **버전 관리**: 모든 수정 내역 추적 및 복구
- **카테고리 관리**: 런너, 길드 분류
- **조회수 추적**: Redis 기반 실시간 조회수
- **인기/랜덤 콘텐츠**: 조회수 기반 인기 항목

### 2. 검색 시스템
- **Elasticsearch 연동**: 한글 형태소 분석 (Nori)
- **부분 일치 검색**: 초성 검색 지원
- **인덱싱**: 비동기 자동 인덱싱

### 3. 실시간 채팅
- **WebSocket 통신**: STOMP 프로토콜
- **세션 관리**: 세션 기반 사용자 식별
- **메시지 저장**: 채팅 기록 영구 보존
- **닉네임 생성**: 자동 닉네임 생성

### 4. 이미지 관리
- **R2 업로드**: Cloudflare R2 통합
- **이미지 변환**: WebP 포맷 자동 변환
- **CDN 연동**: Cloudflare 지원

### 5. 관리자 기능
- **인증 시스템**: 쿠키 기반 세션 관리
- **IP 차단**: 악성 사용자 차단
- **콘텐츠 관리**: 사전 항목 관리
- **통계 조회**: 사용 통계 및 로그

## 🛠 기술 스택

### Core Framework
- **Spring Boot 3.4.5**
- **Java 21**
- **Gradle 8.12** - 빌드 도구

### 데이터 저장소
- **MySQL 8.0** - 메인 데이터베이스
- **Redis** - 캐싱 & 세션 관리
- **Elasticsearch 8.x** - 검색 엔진
- **Cloudflare R2** - 이미지 저장소

### 데이터 접근
- **Spring Data JPA** - ORM
- **Hibernate** - JPA 구현체

### 실시간 통신
- **Spring WebSocket** - WebSocket 지원
- **STOMP** - 메시징 프로토콜
- **SockJS** - 폴백 지원

### 보안
- **CORS** - Cross-Origin Resource Sharing
- **IP 기반 인증** - 관리자 접근 제어

### 모니터링 & 로깅
- **Spring Actuator** - 애플리케이션 메트릭
- **Prometheus** - 메트릭 수집
- **Grafana** - 메트릭 시각화
- **Promtail** - 로그 수집
- **Logback** - 로깅 프레임워크

### 문서화
- **Spring REST Docs** - API 문서 자동 생성
- **AsciiDoc** - 문서 포맷

### 테스트
- **JUnit 5** - 단위 테스트
- **Mockito** - 모킹 프레임워크
- **Testcontainers** - 통합 테스트
- **RestAssured** - API 테스트
- **DataFaker** - 테스트 데이터 생성

### 기타 라이브러리
- **Lombok** - 보일러플레이트 코드 제거
- **Scrimage** - 이미지 처리

## 🚀 시작하기

### 필수 요구사항

- JDK 21 이상
- Gradle 8.12 이상
- MySQL 8.0 이상
- Redis 6.0 이상
- Elasticsearch 8.x (선택)
- Docker & Docker Compose (개발 환경)

### 개발 환경 설정

#### 1. 저장소 클론

```bash
git clone https://github.com/openmpy/taleswiki.git
cd taleswiki/server
```

#### 2. 인프라 실행 (Docker Compose)

```bash
# 개발용 인프라 실행 (MySQL, Redis, Elasticsearch)
docker-compose -f docker/compose-dev.yml up -d

# 상태 확인
docker-compose -f docker/compose-dev.yml ps
```

#### 3. 환경 변수 설정

`application.yml` 설정 또는 환경 변수:

```yaml
server:
  port: 8080

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/taleswiki?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: username
    password: password
  jpa:
    database-platform: org.hibernate.dialect.MySQL8Dialect
    hibernate:
      ddl-auto: none
    properties:
      hibernate:
        default_batch_fetch_size: 100
    open-in-view: false
  servlet:
    multipart:
      max-file-size: 20MB
      max-request-size: 40MB
  data:
    redis:
      host: localhost
      port: 6379
      timeout: 5000

management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always

cors:
  path-pattern: /**
  origins: origins
  methods: POST, GET, PUT, PATCH, DELETE, OPTIONS
  headers: "*"
  allow-credentials: true
  max-age: 3600

cookie:
  http-only: true
  secure: true
  domain: domain
  path: /
  same-site: none
  max-age: 7D

admin:
  nickname: nickname
  password: password
  token: success

image:
  upload-path: path

s3:
  endpoint: endpoint
  access-key: access-key
  secret-key: secret-key
  region: auto
```

#### 4. 애플리케이션 실행

```bash
# Gradle로 실행
./gradlew bootRun

# 또는 JAR 빌드 후 실행
./gradlew build
java -jar build/libs/taleswiki-0.0.1-SNAPSHOT.jar
```

### 프로덕션 설정

```bash
# 프로덕션 프로파일로 실행
java -jar -Dspring.profiles.active=prod taleswiki.jar

# 또는 환경 변수로 설정
export SPRING_PROFILES_ACTIVE=prod
java -jar taleswiki.jar
```

## 📁 프로젝트 구조

```
server/
├── src/
│   ├── main/
│   │   ├── java/com/openmpy/taleswiki/
│   │   │   ├── TaleswikiApplication.java    # 메인 클래스
│   │   │   ├── admin/                       # 관리자 도메인
│   │   │   │   ├── application/             # 응용 계층
│   │   │   │   │   ├── AdminCommandService.java
│   │   │   │   │   └── AdminQueryService.java
│   │   │   │   ├── domain/                  # 도메인 계층
│   │   │   │   │   ├── entity/
│   │   │   │   │   │   └── Blacklist.java
│   │   │   │   │   ├── repository/
│   │   │   │   │   │   └── BlacklistRepository.java
│   │   │   │   │   └── BlacklistReason.java
│   │   │   │   ├── dto/                     # DTO
│   │   │   │   │   ├── request/
│   │   │   │   │   └── response/
│   │   │   │   └── presentation/            # 표현 계층
│   │   │   │       ├── AdminCommandController.java
│   │   │   │       └── AdminQueryController.java
│   │   │   ├── chat/                        # 채팅 도메인
│   │   │   │   ├── application/
│   │   │   │   ├── domain/
│   │   │   │   ├── dto/
│   │   │   │   └── presentation/
│   │   │   ├── common/                      # 공통 모듈
│   │   │   │   ├── application/
│   │   │   │   │   ├── ImageS3Service.java
│   │   │   │   │   └── RedisService.java
│   │   │   │   ├── config/                  # 설정
│   │   │   │   │   ├── JpaAuditingConfig.java
│   │   │   │   │   ├── RedisConfig.java
│   │   │   │   │   ├── S3Config.java
│   │   │   │   │   ├── WebMvcConfig.java
│   │   │   │   │   └── WebSocketConfig.java
│   │   │   │   ├── domain/
│   │   │   │   ├── dto/
│   │   │   │   ├── exception/               # 예외 처리
│   │   │   │   │   ├── CustomException.java
│   │   │   │   │   ├── ErrorResponse.java
│   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   ├── infrastructure/          # 인프라
│   │   │   │   ├── properties/              # 설정 속성
│   │   │   │   └── util/                    # 유틸리티
│   │   │   └── dictionary/                  # 사전 도메인
│   │   │       ├── application/
│   │   │       │   ├── DictionaryCommandService.java
│   │   │       │   ├── DictionaryQueryService.java
│   │   │       │   ├── DictionaryScheduler.java
│   │   │       │   └── DictionarySearchService.java
│   │   │       ├── domain/
│   │   │       │   ├── constants/
│   │   │       │   ├── document/
│   │   │       │   ├── entity/
│   │   │       │   └── repository/
│   │   │       ├── dto/
│   │   │       └── presentation/
│   │   └── resources/
│   │       ├── application.yml              # 애플리케이션 설정
│   │       ├── logback-spring.xml           # 로깅 설정
│   │       └── elasticsearch/
│   │           └── index-settings.json      # ES 인덱스 설정
│   ├── test/                                # 테스트 코드
│   └── docs/                                # API 문서
├── gradle/                                  # Gradle 래퍼
├── docker/                                  # Docker 설정
│   └── compose-dev.yml
├── monitoring/                              # 모니터링 설정
│   ├── compose.yml
│   ├── grafana/
│   ├── prometheus/
│   └── promtail/
├── build.gradle                             # Gradle 빌드 설정
└── settings.gradle
```

### 아키텍처 패턴

프로젝트는 **레이어드 아키텍처**와 **DDD(Domain-Driven Design)** 패턴을 따릅니다:

1. **Presentation Layer** (표현 계층)
   - REST 컨트롤러
   - 요청/응답 처리

2. **Application Layer** (응용 계층)
   - 비즈니스 로직 조정
   - 트랜잭션 관리
   - CQRS 패턴 (Command/Query 분리)

3. **Domain Layer** (도메인 계층)
   - 엔티티 & 값 객체
   - 도메인 서비스
   - 리포지토리 인터페이스

4. **Infrastructure Layer** (인프라 계층)
   - 리포지토리 구현
   - 외부 서비스 연동

## 📖 API 문서

### API 문서 생성 및 확인

```bash
# API 문서 생성
./gradlew clean test

# 서버 실행 후 접속
http://localhost:8080/docs/index.html
```

### 주요 API 엔드포인트

#### 사전(Dictionary) API

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/dictionaries/latest-modified` | 최근 수정된 사전 목록 |
| GET | `/api/v1/dictionaries/categories/{category}` | 카테고리별 사전 목록 |
| GET | `/api/v1/dictionaries/{id}` | 사전 상세 조회 |
| GET | `/api/v1/dictionaries/{id}/history` | 사전 수정 이력 |
| GET | `/api/v1/dictionaries/search?title={title}` | 사전 검색 |
| GET | `/api/v1/dictionaries/random` | 랜덤 사전 조회 |
| GET | `/api/v1/dictionaries/popular` | 인기 사전 목록 |
| POST | `/api/v1/dictionaries` | 사전 생성 |
| PUT | `/api/v1/dictionaries/{id}` | 사전 수정 |

#### 관리자(Admin) API

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/admin/signin` | 관리자 로그인 |
| GET | `/api/v1/admin/dictionaries` | 모든 사전 목록 (관리용) |
| GET | `/api/v1/admin/dictionaries/histories` | 모든 수정 이력 |
| GET | `/api/v1/admin/blacklist` | 차단 IP 목록 |
| POST | `/api/v1/admin/blacklist` | IP 차단 추가 |
| DELETE | `/api/v1/admin/blacklist/{id}` | IP 차단 해제 |

#### 채팅(Chat) API

| Method | Endpoint | Description |
|--------|----------|-------------|
| WebSocket | `/ws` | WebSocket 연결 |
| STOMP | `/topic/messages` | 메시지 구독 |
| STOMP | `/app/chat/send` | 메시지 전송 |
| GET | `/api/v1/chat/messages` | 채팅 기록 조회 |

#### 이미지(Image) API

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/images` | 이미지 업로드 |

### API 응답 형식

#### 성공 응답

```json
{
  "id": 1,
  "title": "런너 이름",
  "category": "RUNNER",
  "content": "내용...",
  "author": "작성자",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

#### 에러 응답

```json
{
  "message": "사전을 찾을 수 없습니다."
}
```

#### 페이징 응답

```json
{
  "content": [...],
  "totalElements": 100,
  "totalPages": 10,
  "number": 0,
  "size": 10,
  "first": true,
  "last": false
}
```

## 🗄 데이터베이스 스키마

### 주요 테이블

#### dictionary (사전)

| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | PK, 자동 증가 |
| title | VARCHAR(255) | 제목 |
| category | VARCHAR(50) | 카테고리 (RUNNER, GUILD) |
| status | VARCHAR(50) | 상태 (ACTIVE, INACTIVE) |
| view_count | BIGINT | 조회수 |
| current_history_id | BIGINT | 현재 버전 ID |
| created_at | DATETIME | 생성일시 |
| updated_at | DATETIME | 수정일시 |

#### dictionary_history (사전 이력)

| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | PK, 자동 증가 |
| dictionary_id | BIGINT | FK, 사전 ID |
| content | TEXT | 내용 |
| author | VARCHAR(255) | 작성자 |
| version | INT | 버전 번호 |
| size | INT | 콘텐츠 크기 |
| created_at | DATETIME | 생성일시 |

#### chat_message (채팅 메시지)

| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | PK, 자동 증가 |
| sender | VARCHAR(45) | 발신자 IP |
| content | TEXT | 메시지 내용 |
| nickname | VARCHAR(100) | 닉네임 |
| session_id | VARCHAR(255) | 세션 ID |
| created_at | DATETIME | 생성일시 |

#### blacklist (차단 목록)

| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | PK, 자동 증가 |
| ip | VARCHAR(45) | 차단 IP |
| reason | VARCHAR(50) | 차단 사유 |
| created_at | DATETIME | 차단일시 |

### 인덱스

```sql
-- 사전 검색 최적화
CREATE INDEX idx_dictionary_title ON dictionary(title);
CREATE INDEX idx_dictionary_category ON dictionary(category);
CREATE INDEX idx_dictionary_status ON dictionary(status);

-- 이력 조회 최적화
CREATE INDEX idx_history_dictionary ON dictionary_history(dictionary_id);

-- 채팅 조회 최적화
CREATE INDEX idx_chat_created ON chat_message(created_at);
```

## 📊 모니터링

### Prometheus + Grafana 설정

```bash
# 모니터링 스택 실행
cd monitoring
docker-compose up -d

# 접속 URL
# Prometheus: http://localhost:9090
# Grafana: http://localhost:3000 (admin/openmpy)
```

### 주요 메트릭

1. **애플리케이션 메트릭**
   - JVM 메모리 사용량
   - GC 통계
   - Thread 상태
   - HTTP 요청 통계

2. **인프라 메트릭**
   - 데이터베이스 연결 풀

### 로그 수집 (Promtail)

```yaml
# promtail-config.yaml
clients:
  - url: http://loki:3100/loki/api/v1/push

scrape_configs:
  - job_name: taleswiki
    static_configs:
      - targets:
          - localhost
        labels:
          job: taleswiki
          __path__: /var/log/taleswiki/*.log
```

## 🧪 테스트

### 테스트 실행

```bash
# 전체 테스트 실행
./gradlew test

# 특정 테스트만 실행
./gradlew test --tests "DictionaryServiceTest"
```

### 테스트 구성

#### 단위 테스트

```java
@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class DictionaryCommandServiceTest {

    @Autowired
    private DictionaryRepository repository;
    
    @Autowired
    private DictionaryCommandService service;
    
    @Test
    void 사전_생성_성공() {
        // given
        DictionarySaveRequest request = ...
        
        // when
        DictionarySaveResponse response = service.save(request);
        
        // then
        assertThat(response).isNotNull();
    }
}
```

### 테스트 데이터

```java
// Fixture 클래스 활용
public class Fixture {
    public static Dictionary createDictionary() {
        return Dictionary.builder()
            .title("테스트 사전")
            .category(DictionaryCategory.RUNNER)
            .build();
    }
}
```

## 🚀 배포

### JAR 빌드

```bash
# 프로덕션 빌드
./gradlew clean build -x test

# 빌드 결과물
ls build/libs/taleswiki-*.jar
```

### Docker 이미지 빌드

```dockerfile
# Dockerfile
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY build/libs/*.jar app.jar

ENV JAVA_OPTS="-Xmx512m -Xms256m"
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

```bash
# 이미지 빌드
docker build -t taleswiki-server:latest .

# 컨테이너 실행
docker run -d \
  --name taleswiki-server \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:mysql://db:3306/taleswiki \
  taleswiki-server:latest
```

## 🔧 트러블슈팅

### 일반적인 문제

#### 1. 데이터베이스 연결 실패

```bash
# MySQL 컨테이너 상태 확인
docker ps | grep mysql

# 로그 확인
docker logs mysql-container

# 연결 테스트
mysql -h localhost -P 3306 -u root -p
```

#### 2. Redis 연결 문제

```bash
# Redis 연결 테스트
redis-cli ping

# 메모리 사용량 확인
redis-cli info memory
```

#### 3. Elasticsearch 인덱싱 실패

```bash
# 인덱스 상태 확인
curl -X GET "localhost:9200/_cat/indices?v"

# 매핑 확인
curl -X GET "localhost:9200/dictionary/_mapping"
```

### 성능 튜닝

#### 1. JVM 최적화

```bash
# 힙 메모리 설정
java -Xms1g -Xmx2g -jar app.jar

# GC 로그 활성화
java -XX:+PrintGCDetails -XX:+PrintGCDateStamps -jar app.jar
```

#### 2. 데이터베이스 튜닝

```sql
-- 슬로우 쿼리 확인
SHOW PROCESSLIST;

-- 인덱스 사용 확인
EXPLAIN SELECT * FROM dictionary WHERE title = '...';
```

## 📝 코드 스타일 가이드

### Java 코딩 컨벤션

- **네이밍**: 카멜케이스, 의미있는 이름
- **패키지**: 도메인별 패키지 구조
- **클래스**: 단일 책임 원칙

### Git 커밋 메시지

```
feat: 사용자 인증 기능 추가
fix: 사전 검색 버그 수정
docs: README 업데이트
refactor: 서비스 레이어 리팩토링
test: 단위 테스트 추가
```

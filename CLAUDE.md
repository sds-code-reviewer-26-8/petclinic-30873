# CLAUDE.md

Spring Boot 기반 Pet Clinic(JPA) 샘플 프로젝트. 현재는 조회(GET) 전용 REST API.

## 기술 스택
- Java 17 (Gradle toolchain), Gradle Kotlin DSL (`build.gradle.kts`, wrapper 사용)
- Spring Boot 4.1.1, `io.spring.dependency-management`
- Spring Data JPA, Spring WebMVC, Thymeleaf (의존성만 있고 템플릿 없음)
- springdoc-openapi-starter-webmvc-ui 3.1.1 (Swagger UI)
- H2 인메모리 DB (runtimeOnly), 개발용: h2console, devtools
- 테스트: JUnit Platform (`PetclinicApplicationTests` 컨텍스트 로드 테스트 1개)
- 프로젝트명(`description`): `petclinic-jpa`, 그룹 `com.example`

## 명령어
```
./gradlew bootRun     # 실행 (Windows: gradlew.bat bootRun)
./gradlew build       # 빌드 + 테스트
./gradlew test        # 테스트만
```

## 패키지 구조 (`src/main/java/com/example/petclinic`)
- `PetclinicApplication` — 진입점
- `LoadDatabase` — `CommandLineRunner`로 시작 시 샘플 데이터 적재 (vet 6, speciality 3, vet_speciality 5, type 6, owner 10, pet 13, visit 4, member `admin`/`{noop}admin` + 역할 3개)
- `controller/` — `@RestController`, 경로 `/api/v1/...`
  - `controller/response/` — 응답 DTO (`*Dto`), 컨트롤러에서 엔티티→DTO 수동 변환
- `service/` — `@Service` + 클래스 레벨 `@Transactional(readOnly = true)`, 생성자 주입. 미존재 시 `NoSuchElementException` 발생 (`verifyXxxExists` 패턴)
- `repository/` — Spring Data JPA 리포지토리 (엔티티당 1개)
- `entity/` — JPA 엔티티

## REST 엔드포인트 (모두 GET: 목록 `/`, 상세 `/{id}`)
| 경로 | 컨트롤러 |
|---|---|
| `/api/v1/owners` | OwnerController |
| `/api/v1/pets` | PetController |
| `/api/v1/types` | PetTypeController |
| `/api/v1/vets` | VetController |
| `/api/v1/specialities` | SpecialityController |
| `/api/v1/visits` | VisitController |
| `/api/v1/members` | MemberController (상세는 `/{username}`) |

## 도메인 모델
- `Person` (`people` 테이블, SINGLE_TABLE 상속) ← `Owner`, `Vet`
- `Pet` (`pets`) — ManyToOne(LAZY) `type`(PetType), `owner`(Owner); `birthDate`는 `LocalDateTime`
- `PetType` (`types`), `Speciality` (`specialities`)
- `VetSpeciality` (`vet_specialities`) — Vet–Speciality 연결 엔티티 (ManyToOne 두 개)
- `Visit` (`visit`) — ManyToOne(LAZY) `pet`, `date`, `description`
- `Member` (`members`, PK `username`) / `Role` (`roles`, ManyToOne `member`) — 인증용 사용자/권한 (Spring Security 의존성은 아직 없음)

## 컨벤션
- 엔티티: 필드 + 명시적 getter/setter (Lombok 미사용), ID는 `@GeneratedValue(IDENTITY)`, 연관관계는 `FetchType.LAZY`
- 의존성 주입은 생성자 주입
- 컬럼명은 `@Column(name = ...)`으로 snake_case 지정 (예: `pet_id`, `birth_date`)

## 참고 / 주의
- `src/main/resources/application.yml`은 현재 비어 있음 (기본 설정 사용, H2 인메모리 DB)
- git 저장소 아님 (`.gitignore`만 존재)
- 컨트롤러가 DTO 변환 로직을 직접 포함하므로 새 엔드포인트도 같은 패턴을 따를 것

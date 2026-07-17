이 문서는 Spring Boot + JPA 기반 백엔드 서비스를 개발할 때 팀 전체가 따라야 하는 공통 규칙을 정의한다.

이 문서는 다음 목적을 가진다.

- 팀 내 코드 스타일과 구조를 통일한다.
- 신규 개발자가 빠르게 프로젝트 구조를 이해할 수 있도록 한다.
- 코드 리뷰 기준을 명확히 한다.
- 여러 서비스에서 동일한 개발 패턴을 유지한다.

# 1. 패키지 구조

패키지는 전역 공통으로 쓰는 global 패키지와, 각 도메인에 해당하는 패키지들로 분리한다.

## 1.1 특정 도메인의 패키지 구조 예시

```
com.manruhomerun.yadan.{domainName}.controller
com.manruhomerun.yadan.{domainName}.service
com.manruhomerun.yadan.{domainName}.repository
com.manruhomerun.yadan.{domainName}.dto
com.manruhomerun.yadan.{domainName}.domain
com.manruhomerun.yadan.{domainName}.domain.entity
com.manruhomerun.yadan.{domainName}.domain.enums
com.manruhomerun.yadan.{domainName}.error

```

## 1.2 global 패키지 구조 예시

```
com.manruhomerun.yadan.global.dto
com.manruhomerun.yadan.global.error
com.manruhomerun.yadan.global.config
```

# 2. Controller 설계 규칙

Controller는 API의 진입점 역할만 수행한다. 따라서, Controller의 책임은 다음 세 가지로 제한한다.

1. 요청 입력 수신
2. 서비스 호출
3. 공통 응답 반환

## 2. 1 Controller 금지사항

Controller쪽 코드에서는 다음 작업을 금지한다.

- JSON 응답 직접 조립
- try/catch 기반 예외 처리
- 비즈니스 로직 작성
- Swagger를 위한 API 응답 예시 하드코딩

## 2.2 Controller Swagger 작성 방법

- @Operation과 @ApiResponses를 사용한다.
- 다만, summary만 작성하는 것을 기본으로 하고 description은 꼭 부연설명이 필요한 상황에서만 작성한다.
- @ApiResponse에 응답 예시를 json으로 하드코딩하지 않고, DTO 클래스를 적는다.

```json
// 예시

@Operation(summary = "인스타그램 MBTI예측")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "MBTI 조회 성공", content = @Content(schema = @Schema(implementation = MbtiPredictedDto.class)))})

```

# 3. DTO 규칙

DTO는 기본적으로 `record`를 사용한다.

```java
public record UserResponse(
    String id,
    String name
) {}
```

## 3. 1 DTO Swagger 작성 규칙

DTO 필드에는 Swagger Schema를 작성한다.

```java
@Schema(description = "사용자명", example = "홍길동")
String name
```

## 3.2 DTO 작명규칙

- 응답에 사용되는 DTO는 `Response`로 끝난다.
- 요청에 사용되는 DTO는 `Request`로 끝난다.

## 3.3 DTO 금지사항

다음 패턴을 금지한다.

- Entity를 그대로 API 응답으로 반환
- Swagger 작성을 위한 DTO 생성

## 3.4 페이지네이션 DTO 규칙

- 페이지네이션 응답은 도메인별 DTO를 중복 생성하지 않고 `com.manruhomerun.yadan.global.dto.PageResponse<T>`를 공용으로 사용한다.
- 각 도메인은 페이지 내부 `contents`에 들어갈 아이템 DTO만 정의한다.
- 공용 페이지네이션 DTO 생성은 `PageResponse.from(page, contents)` 형태로 처리한다.
- Swagger에서 제네릭 페이지네이션 응답을 명시할 때는 `@Schema(implementation = PageResponse.class)`를 직접 지정하지 않는다.
- 제네릭 내부 타입까지 Swagger에 노출해야 하는 경우, 컨트롤러 메서드의 실제 반환 타입을 사용하도록 `@ApiResponse(..., useReturnTypeSchema = true)` 또는 반환 타입 추론 방식을 사용한다.

```java
public record PageResponse<T>(
    List<T> contents,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages
) {}
```

# 4. Entity 설계 규칙

Entity는 도메인 상태와 영속성 책임만 가진다.

## 4.1 ID (pk) 규칙

- ID는 UUID 또는 Long이다. 어떤 타입을 써야 하는지는 사용자의 명령을 참고하라.
- Long 타입으로 ID를 지정하는 경우 사용자의 특별한 지시가 없다면 auto increment를 적용한다.
- UUID를 ID로 사용할 경우 String 타입 사용, Java UUID 타입 사용 금지

## 4.2 연관관계 설정 규칙

- 사용자가 명시하지 않는다면 모든 연관관계는 FetchType.LAZY로 설정

    ```java
    @ManyToOne(fetch = FetchType.LAZY)
    ```

- Enum 컬럼 작성 규칙

    ```java
    @Enumerated(EnumType.STRING)
    ```


# 5. DTO / Entity 변환 규칙

DTO와 Entity 변환 책임을 명확히 분리한다. Entity는 DTO를 의존하지 않는다.
따라서, 다음과 같은 패턴을 금지한다.

```java
Entity.from(RequestDto)
Entity.to(ResponseDto)
```

## 5.1 Request DTO → Entity

```java
RequestDto.toEntity(...)
```

## 5.2 Entity → Response DTO

```java
ResponseDto.from(entity)
```

# 6. Service 계층 규칙

Service 계층은 실제 비즈니스 로직을 담당한다.

Service의 책임

- 입력 검증
- 참조 엔티티 조회
- 엔티티 생성
- 데이터 저장
- Response DTO 반환

# 7. 예외처리 규칙

## 7.1 기본 규칙

예외는 두 가지로 분류한다.

- Domain Exception: 특정 도메인에서만 발생하는 예외
- Global Exception: 모든 도메인에서 발생 가능한 예외

모든 예외는 CustomException을 상속한다.

```java
package com.manruhomerun.yadan.global.error

public class CustomException extends RuntimeException {

    private final BaseErrorCode errorCode;

    protected CustomException(BaseErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BaseErrorCode getErrorCode() {
        return errorCode;
    }
}
```

### 7.1.1 Domain Exception

- 각 도메인에서만 발생하는 예외들
- 각 도메인의 예외들은 해당 도메인의 error 패키지 내에 생성되며, 각각 `CustomException`을 상속한다.
- 각 예외명은 어떤 에러인지 잘 알 수 있도록 구성한다.

### 7.1.2 Global Exception

- 여러 도메인에서 공통으로 발생하는 예외들
- 공통 예외는 `com.manruhomerun.yadan.global.error` 하위에 생성되며, 각각 `CustomException`을 상속한다.
- 각 예외명은 어떤 에러인지 잘 알 수 있도록 구성한다.

## 7.2 ErrorCode 규칙

`CustomException`의 `BaseErrorCode`에 대해 설명한다.

BaseErrorCode는 아래와 같다.

```java
public interface BaseErrorCode {

    String getCode();

    String getDefaultMessage();

    int getStatus();
}
```

- 각 도메인에서는 이를 `{도메인명}ErrorCode` enum에서 구현하여 사용한다.
- 공통 예외를 위한 에러코드는 `CommonErrorCode`에서 구현하여 사용한다.

도메인 에러 코드 예시

```java
public enum CardErrorCode implements BaseErrorCode {
    CARD_ALREADY_EXISTS("CARD_409_ALREADY_EXISTS", "이미 카드가 존재합니다.", 409),
    CARD_NOT_FOUND("CARD_404", "카드를 찾을 수 없습니다.", 404),
    CARD_HISTORY_NOT_FOUND("CARD_HISTORY_404", "카드 거래 내역을 찾을 수 없습니다.", 404),
    FRANCHISE_NOT_FOUND("FRANCHISE_404", "가맹점을 찾을 수 없습니다.", 404),
    PAYMENT_FAILED("PAYMENT_409", "결제 처리에 실패했습니다.", 409);

    private final String code;
    private final String defaultMessage;
    private final int status;

    CardErrorCode(String code, String defaultMessage, int status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public int getStatus() {
        return status;
    }
}

```

## 7.3 Global Exception Handler

전역 예외 처리는 `@RestControllerAdvice`를 사용한다.

도메인 예외는 부모 예외 단위로 처리한다.

```java
@ExceptionHandler(DomainException.class)
```

미처리 예외는 다음으로 처리한다.

```java
@ExceptionHandler(Exception.class)
```

## **7.4 예외 시 응답 DTO**

예외 발생 시 API 응답 body는 모든 컨트롤러에서 동일한 형식을 사용한다.

예외 응답 DTO는 `global.dto` 패키지에 `ErrorResponse`라는 이름으로 생성한다.

```java
package com.manruhomerun.yadan.global.dto;

import com.manruhomerun.yadan.global.error.BaseErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공통 예외 응답")
public record ErrorResponse(

    @Schema(description = "에러 코드", example = "TRAVEL_404")
    String code,

    @Schema(description = "에러 메시지", example = "여행지를 찾을 수 없습니다.")
    String message,

    @Schema(description = "요청 경로", example = "/api/v1/travels/1")
    String path

) {

    public static ErrorResponse of(BaseErrorCode errorCode, String message, String path) {
        return new ErrorResponse(
            errorCode.getCode(),
            message,
            path
        );
    }
}
```

예외 응답 body 예시는 다음과 같다.

```json
{
  "code": "TRAVEL_404",
  "message": "여행지를 찾을 수 없습니다.",
  "path": "/api/v1/travels/1"
}
```

각 필드의 의미는 다음과 같다.

- `code`: 프론트엔드에서 에러 상황을 구분하기 위한 고유 에러 코드이다.
- `message`: 사용자 또는 개발자가 확인할 수 있는 에러 메시지이다.
- `path`: 예외가 발생한 요청 경로이다.

Controller에서는 예외 응답 DTO를 직접 생성하지 않는다.

예외 응답은 반드시 `GlobalExceptionHandler`에서 생성하여 반환한다.

```java
@ExceptionHandler(CustomException.class)
public ResponseEntity<ErrorResponse> handleCustomException(
        CustomException e,
        HttpServletRequest request
) {
    BaseErrorCode errorCode = e.getErrorCode();

    return ResponseEntity
        .status(errorCode.getStatus())
        .body(ErrorResponse.of(
            errorCode,
            e.getMessage(),
            request.getRequestURI()
        ));
}
```

Swagger에서 예외 응답을 명시할 때는 `ErrorResponse.class`를 사용한다.

```java
@ApiResponse(
    responseCode = "404",
    description = "리소스를 찾을 수 없음",
    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
)
```

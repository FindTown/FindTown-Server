package yapp.exception.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class ErrorCodes {

  private ErrorCode errorCode;
  private String message;

  public ErrorCodes(
    ErrorCode errorCode,
    String message
  ) {
    this.errorCode = errorCode;
    this.message = message;
  }

  public static ErrorCodes INTERNAL_SERVER_ERROR() {
    return new ErrorCodes(ErrorCode.INTERNAL_SERVER_ERROR, "정의되지 않은 서버 에러입니다.");
  }

  public static ErrorCodes INVALID_INPUT_VALUE() {
    return new ErrorCodes(ErrorCode.INVALID_INPUT_VALUE, "잘못된 입력 방식입니다.");
  }

  public static ErrorCodes INVALID_TYPE_VALUE() {
    return new ErrorCodes(ErrorCode.INVALID_TYPE_VALUE, "유효한 타입이 아닙니다.");
  }

  public static ErrorCodes UN_AUTHORIZED() {
    return new ErrorCodes(ErrorCode.UN_AUTHORIZED, "인증 실패하였습니다.");
  }

  public static ErrorCodes METHOD_NOT_ALLOWED() {
    return new ErrorCodes(ErrorCode.METHOD_NOT_ALLOWED, "지원하지 않은 HTTP 메서드입니다.");
  }

  public static ErrorCodes HANDLE_ACCESS_DENIED() {
    return new ErrorCodes(ErrorCode.HANDLE_ACCESS_DENIED, "인증 권한을 보유하지 않습니다");
  }

  public static ErrorCodes ENTITY_NOT_FOUND(String message) {
    return new ErrorCodes(ErrorCode.ENTITY_NOT_FOUND, message);
  }

  public static ErrorCodes DUPLICATED_NICKNAME_VALUE(String message) {
    return new ErrorCodes(ErrorCode.DUPLICATED_NICKNAME_VALUE, message);
  }

  public static ErrorCodes DUPLICATED_MEMBER_VALUE(String message) {
    return new ErrorCodes(ErrorCode.DUPLICATED_MEMBER_VALUE, message);
  }

  public static ErrorCodes SIGNUP_FAIL(String message) {
    return new ErrorCodes(ErrorCode.SIGNUP_FAIL, message);
  }

  public static ErrorCodes INVALID_TOKEN(String message) {
    return new ErrorCodes(ErrorCode.INVALID_TOKEN_EXPIRED, message);
  }

  @Getter
  public enum ErrorCode {

    //500
    INTERNAL_SERVER_ERROR("SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_ERROR("SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    NO_POINT("SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_SEND_FAIL("SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_EVENT("SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),
    FAILED_PAYMENT("SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR),

    //400
    INVALID_INPUT_VALUE("REQUEST_ERROR", HttpStatus.BAD_REQUEST),
    INVALID_TYPE_VALUE("REQUEST_ERROR", HttpStatus.BAD_REQUEST),
    DUPLICATED_NICKNAME_VALUE("MEMBER_ERROR", HttpStatus.BAD_REQUEST),

    //401
    UN_AUTHORIZED("AUTHORIZED_ERROR", HttpStatus.UNAUTHORIZED),
    ENTITY_NOT_FOUND("MEMBER_ERROR", HttpStatus.UNAUTHORIZED),
    SIGNUP_FAIL("MEMBER_ERROR", HttpStatus.UNAUTHORIZED),
    DUPLICATED_MEMBER_VALUE("MEMBER_ERROR", HttpStatus.UNAUTHORIZED),

    INVALID_TOKEN_EXPIRED("AUTHORIZED_ERROR", HttpStatus.UNAUTHORIZED),

    //405
    METHOD_NOT_ALLOWED("METHOD_ERROR", HttpStatus.METHOD_NOT_ALLOWED),

    //403
    HANDLE_ACCESS_DENIED("HANDLE_ERROR", HttpStatus.FORBIDDEN);

    private final HttpStatus status;

    private final String code;

    ErrorCode(
      String code,
      HttpStatus status
    ) {
      this.code = code;
      this.status = status;
    }
  }

}
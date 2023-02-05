package yapp.exception.base;

import yapp.exception.model.ErrorCodes;

public class BusinessException extends RuntimeException {

  private final ErrorCodes errorCode;

  public BusinessException(
    String message,
    ErrorCodes errorCode
  ) {
    super(message);
    this.errorCode = errorCode;
  }

  public BusinessException(ErrorCodes errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  public ErrorCodes getErrorCode() {
    return errorCode;
  }

}

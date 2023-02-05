package yapp.exception.base.member;

import yapp.exception.base.BusinessException;
import yapp.exception.base.InvalidInputException;
import yapp.exception.model.ErrorCodes;

public class MemberException {

  public static class MemberNotFound extends BusinessException {
    public MemberNotFound(String... message) {
      super(ErrorCodes.ENTITY_NOT_FOUND(String.join(", ", message)));
    }
  }

  public static class NickNameDuplicated extends InvalidInputException {
    public NickNameDuplicated(String... message) {
      super(ErrorCodes.DUPLICATED_NICKNAME_VALUE(String.join(", ", message)));
    }
  }

  public static class MemberSignUpFail extends BusinessException {
    public MemberSignUpFail(String... message) {
      super(ErrorCodes.SIGNUP_FAIL(String.join(", ", message)));
    }
  }

  public static class DuplicateMember extends BusinessException {
    public DuplicateMember(String... messgage) {
      super(ErrorCodes.DUPLICATED_MEMBER_VALUE(String.join(", ", messgage)));
    }
  }

  public static class MemberTokenExpired extends BusinessException {
    public MemberTokenExpired(String... message) {
      super(ErrorCodes.INVALID_TOKEN(String.join(", ", message)));
    }
  }

  public static class InvalidToken extends BusinessException {
    public InvalidToken(String... message) {
      super(ErrorCodes.INVALID_TOKEN(String.join(", ", message)));
    }
  }

}

package yapp.common.oauth.exception;

public class TokenValidFailedException extends RuntimeException {

  public TokenValidFailedException() {
    super("토큰 생성 에러");
  }

  private TokenValidFailedException(String message) {
    super(message);
  }
}

package yapp.common.response;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {

  private final static int SUCCESS = 200;
  private final static int NOT_FOUND = 400;
  private final static int FAILED = 500;
  private final static String SUCCESS_MESSAGE = "SUCCESS";
  private final static String NOT_FOUND_MESSAGE = "NOT FOUND";
  private final static String FAILED_MESSAGE = "서버에서 오류가 발생하였습니다.";
  private final static String INVALID_ACCESS_TOKEN = "유효하지 않은 ACCESS TOKEN 입니다.";
  private final static String INVALID_REFRESH_TOKEN = "유효하지 않은 REFRESH TOKEN 입니다.";
  private final static String NOT_EXPIRED_TOKEN_YET = "아직 토큰이 만료되지 않았습니다.";

  private final ApiResponseHeader header;
  private final Map<String, T> body;

  public static <T> ApiResponse<T> success(
    String name,
    T body
  ) {
    Map<String, T> map = new HashMap<>();
    map.put(name, body);

    return new ApiResponse(new ApiResponseHeader(SUCCESS, SUCCESS_MESSAGE), map);
  }

  public static <T> ApiResponse<T> success(Map<String, T> map) {
    return new ApiResponse(new ApiResponseHeader(SUCCESS, SUCCESS_MESSAGE), map);
  }

  public static <T> ApiResponse<T> fail() {
    return new ApiResponse(new ApiResponseHeader(FAILED, FAILED_MESSAGE), null);
  }

  public static <T> ApiResponse<T> invalidAccessToken() {
    return new ApiResponse(new ApiResponseHeader(FAILED, INVALID_ACCESS_TOKEN), null);
  }

  public static <T> ApiResponse<T> invalidRefreshToken() {
    return new ApiResponse(new ApiResponseHeader(FAILED, INVALID_REFRESH_TOKEN), null);
  }

  public static <T> ApiResponse<T> notExpiredTokenYet() {
    return new ApiResponse(new ApiResponseHeader(FAILED, NOT_EXPIRED_TOKEN_YET), null);
  }
}

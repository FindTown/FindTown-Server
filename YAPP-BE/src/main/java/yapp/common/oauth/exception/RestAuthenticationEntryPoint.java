package yapp.common.oauth.exception;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
  @Override
  public void commence(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException authException
  ) throws IOException, ServletException {
    authException.printStackTrace();
    log.info("승인되지 않은 오류 : {}", authException.getMessage());
    response.sendError(
      HttpServletResponse.SC_UNAUTHORIZED,
      authException.getLocalizedMessage()
    );
  }
}


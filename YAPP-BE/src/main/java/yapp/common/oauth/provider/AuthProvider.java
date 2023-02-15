package yapp.common.oauth.provider;

import java.util.Map;

public interface AuthProvider {

  Map<String, Object> login(String memberId);
}

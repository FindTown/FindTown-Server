package yapp.exception.base.town;

import yapp.exception.base.BusinessException;
import yapp.exception.model.ErrorCodes;

public class TownException {

  public static class TownNotFound extends BusinessException {
    public TownNotFound(String... message) {
      super(ErrorCodes.ENTITY_NOT_FOUND(String.join(", ", message)));
    }
  }

}

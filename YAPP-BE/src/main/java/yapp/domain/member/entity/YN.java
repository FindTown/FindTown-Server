package yapp.domain.member.entity;

public enum YN {

  Y(true),
  N(false);

  private final boolean value;

  YN(
    boolean value
  ) {
    this.value = value;
  }

  public boolean getValue() {
    return this.value;
  }

  public static YN of(boolean status) {
    return status ? YN.valueOf("Y") : YN.valueOf("N");
  }
}

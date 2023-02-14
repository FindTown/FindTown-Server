package yapp.domain.member.entity;

public enum WishStatus {
  YES,
  NO;

  public static WishStatus of(String wishStatus) {
    return WishStatus.valueOf(wishStatus.toUpperCase());
  }
}

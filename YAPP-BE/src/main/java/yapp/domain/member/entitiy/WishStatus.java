package yapp.domain.member.entitiy;

public enum WishStatus {
  YES,
  NO;

  public static WishStatus of(String wishStatus) {
    return WishStatus.valueOf(wishStatus.toUpperCase());
  }
}

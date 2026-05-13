class TokenManager {
  // 싱글톤 패턴 설정
  static final TokenManager _instance = TokenManager._internal();
  factory TokenManager() => _instance;
  TokenManager._internal();

  String? _accessToken;

  // 토큰 저장
  void setToken(String token) {
    _accessToken = token;
    print("[TokenManager] 토큰 저장 완료: $_accessToken");
  }

  // 토큰 가져오기
  String? get token => _accessToken;

  // 로그인 여부 확인
  bool get hasToken => _accessToken != null;

  // 🔥 [추가] 토큰 삭제 (로그아웃 시 사용)
  void clear() {
    _accessToken = null;
    print("[TokenManager] 토큰이 삭제되었습니다.");
  }
}
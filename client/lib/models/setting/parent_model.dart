// 내 정보 조회 (부모 계정)

class ParentModel {
  final int parentId;
  final String email;
  final String provider;

  ParentModel({
    required this.parentId,
    required this.email,
    required this.provider,
  });

  factory ParentModel.fromJson(Map<String, dynamic> json) {
    return ParentModel(
      // ?? 를 사용해서 만약 null이 오면 기본값을 넣어줍니다.
      parentId: json['parentId'] ?? 0,
      email: json['email'] ?? '이메일 정보 없음',
      provider: json['provider'] ?? 'none',
    );
  }
}
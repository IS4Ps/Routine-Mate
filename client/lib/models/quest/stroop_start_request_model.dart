// stroop 게임 시작 (request)

class StroopStartRequestModel {
  final int childId;
  final int totalCount;
  final int difficulty;

  StroopStartRequestModel({
    required this.childId,
    required this.totalCount,
    required this.difficulty,
  });

  // 서버로 보낼 때 JSON으로 변환하는 메서드
  Map<String, dynamic> toJson() {
    return {
      'childId': childId,
      'totalCount': totalCount,
      'difficulty': difficulty,
    };
  }
}
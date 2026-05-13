// go-nogo 게임 시작 (request)

class GoNoGoStartRequestModel {
  final int childId;
  final int totalCount;
  final int difficulty;

  GoNoGoStartRequestModel({
    required this.childId,
    required this.totalCount,
    required this.difficulty,
  });

  Map<String, dynamic> toJson() => {
    "childId": childId,
    "totalCount": totalCount,
    "difficulty": difficulty,
  };
}
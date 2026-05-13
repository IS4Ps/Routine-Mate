// n-back 게임 시작 (request)

class NBackStartRequestModel {
  final int childId;
  final int nLevel;
  final int totalCount;

  NBackStartRequestModel({
    required this.childId,
    required this.nLevel,
    this.totalCount = 20,
  });

  Map<String, dynamic> toJson() {
    return {
      "childId": childId,
      "nLevel": nLevel,
      "totalCount": totalCount,
    };
  }
}
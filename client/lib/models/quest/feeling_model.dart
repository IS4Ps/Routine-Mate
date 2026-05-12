// 오늘 감정 조회

class FeelingModel {
  final String primaryEmotion;
  final int score;

  FeelingModel({required this.primaryEmotion, required this.score});

  factory FeelingModel.fromJson(Map<String, dynamic> json) {
    return FeelingModel(
      primaryEmotion: json['primaryEmotion'] ?? 'UNKNOWN',
      score: json['score'] ?? 0,
    );
  }
}
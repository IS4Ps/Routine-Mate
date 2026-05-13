// n-back 정답 제출


class NBackSubmitResponseModel {
  final int correctCount;
  final int wrongCount;
  final int totalCount;
  final double accuracy;
  final int score;
  final int rewardGold;
  final int statIntelligenceGain;
  final int nLevel;

  NBackSubmitResponseModel({
    required this.correctCount,
    required this.wrongCount,
    required this.totalCount,
    required this.accuracy,
    required this.score,
    required this.rewardGold,
    required this.statIntelligenceGain,
    required this.nLevel,
  });

  factory NBackSubmitResponseModel.fromJson(Map<String, dynamic> json) {
    final data = json['data'];
    return NBackSubmitResponseModel(
      correctCount: data['correctCount'],
      wrongCount: data['wrongCount'],
      totalCount: data['totalCount'],
      accuracy: (data['accuracy'] as num).toDouble(),
      score: data['score'],
      rewardGold: data['rewardGold'],
      statIntelligenceGain: data['statIntelligenceGain'],
      nLevel: data['nLevel'],
    );
  }
}
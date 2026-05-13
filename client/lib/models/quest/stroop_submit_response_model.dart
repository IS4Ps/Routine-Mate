// stroop 정답 제출 (response)

class StroopSubmitResponseModel {
  final int correctCount;
  final int wrongCount;
  final int totalCount;
  final double accuracy;
  final double avgResponseTime;
  final int score;
  final int rewardGold;
  final int statCreativityGain;

  StroopSubmitResponseModel({
    required this.correctCount,
    required this.wrongCount,
    required this.totalCount,
    required this.accuracy,
    required this.avgResponseTime,
    required this.score,
    required this.rewardGold,
    required this.statCreativityGain,
  });

  factory StroopSubmitResponseModel.fromJson(Map<String, dynamic> json) {
    final data = json['data'];
    return StroopSubmitResponseModel(
      correctCount: data['correctCount'],
      wrongCount: data['wrongCount'],
      totalCount: data['totalCount'],
      accuracy: (data['accuracy'] as num).toDouble(),
      avgResponseTime: (data['avgResponseTime'] as num).toDouble(),
      score: data['score'],
      rewardGold: data['rewardGold'],
      statCreativityGain: data['statCreativityGain'],
    );
  }
}
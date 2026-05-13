// go-nogo 정답 제출

class GoNoGoSubmitResponseModel {
  final int correctCount;
  final int wrongCount;
  final int totalCount;
  final double accuracy;
  final double avgResponseTime;
  final int score;
  final int rewardGold;
  final int statStrengthGain;

  GoNoGoSubmitResponseModel({
    required this.correctCount, required this.wrongCount, required this.totalCount,
    required this.accuracy, required this.avgResponseTime, required this.score,
    required this.rewardGold, required this.statStrengthGain,
  });

  factory GoNoGoSubmitResponseModel.fromJson(Map<String, dynamic> json) {
    var data = json['data'];
    return GoNoGoSubmitResponseModel(
      correctCount: data['correctCount'],
      wrongCount: data['wrongCount'],
      totalCount: data['totalCount'],
      accuracy: data['accuracy'].toDouble(),
      avgResponseTime: data['avgResponseTime'].toDouble(),
      score: data['score'],
      rewardGold: data['rewardGold'],
      statStrengthGain: data['statStrengthGain'],
    );
  }
}
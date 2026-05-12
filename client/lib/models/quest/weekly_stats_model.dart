// 주간 달성률 조회 (퀘스트)

class WeeklyStatsModel {
  final String startDate;
  final String endDate;
  final int successDays;
  final int totalDays;
  final double weeklySuccessRate;
  final double avgCompletionRate;
  final bool isRewardEligible;

  WeeklyStatsModel({
    required this.startDate,
    required this.endDate,
    required this.successDays,
    required this.totalDays,
    required this.weeklySuccessRate,
    required this.avgCompletionRate,
    required this.isRewardEligible,
  });

  factory WeeklyStatsModel.fromJson(Map<String, dynamic> json) {
    return WeeklyStatsModel(
      startDate: json['startDate'] ?? "",
      endDate: json['endDate'] ?? "",
      successDays: json['successDays'] ?? 0,
      totalDays: json['totalDays'] ?? 0,
      weeklySuccessRate: (json['weeklySuccessRate'] ?? 0).toDouble(),
      avgCompletionRate: (json['avgCompletionRate'] ?? 0).toDouble(),
      isRewardEligible: json['isRewardEligible'] ?? false,
    );
  }
}
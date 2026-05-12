// 오프라인 보상 목록 조회

class OfflineRewardListModel {
  final int rewardId;
  final String periodType; // "WEEKLY" 또는 "MONTHLY"
  final int targetDays;
  final String rewardPromiseText;
  final String status;

  OfflineRewardListModel({
    required this.rewardId,
    required this.periodType,
    required this.targetDays,
    required this.rewardPromiseText,
    required this.status,
  });

  factory OfflineRewardListModel.fromJson(Map<String, dynamic> json) {
    return OfflineRewardListModel(
      rewardId: json['rewardId'],
      periodType: json['periodType'],
      targetDays: json['targetDays'] ?? 0,
      rewardPromiseText: json['rewardPromiseText'],
      status: json['status'],
    );
  }
}
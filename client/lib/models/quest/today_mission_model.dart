// 오늘의 미션 목록 조회

class TodayMissionModel {
  final int missionId;
  final String presetTitle;
  final String bigTaskTitle;
  final int assignedExp;
  final String date;
  final String startTime;
  final String endTime;
  final String status; // "PENDING", "STARTED", "COMPLETED" 등
  final String? startedAt;
  final String? completedAt;
  final String? approvedAt;

  TodayMissionModel({
    required this.missionId,
    required this.presetTitle,
    required this.bigTaskTitle,
    required this.assignedExp,
    required this.date,
    required this.startTime,
    required this.endTime,
    required this.status,
    this.startedAt,
    this.completedAt,
    this.approvedAt,
  });

  // JSON 데이터를 객체로 변환하는 팩토리 생성자
  factory TodayMissionModel.fromJson(Map<String, dynamic> json) {
    return TodayMissionModel(
      missionId: json['missionId'],
      presetTitle: json['presetTitle'],
      bigTaskTitle: json['bigTaskTitle'],
      assignedExp: json['assignedExp'],
      date: json['date'],
      startTime: json['startTime'],
      endTime: json['endTime'],
      status: json['status'],
      startedAt: json['startedAt'],
      completedAt: json['completedAt'],
      approvedAt: json['approvedAt'],
    );
  }
}
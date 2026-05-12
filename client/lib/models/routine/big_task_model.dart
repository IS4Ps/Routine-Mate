class SmallTaskModel {
  final int smallTaskId;
  final String title;
  final String tags;
  final String difficultyLevel;
  final int orderIndex;

  SmallTaskModel({
    required this.smallTaskId,
    required this.title,
    required this.tags,
    required this.difficultyLevel,
    required this.orderIndex,
  });

  factory SmallTaskModel.fromJson(Map<String, dynamic> json) {
    return SmallTaskModel(
      smallTaskId: json['smallTaskId'],
      title: json['title'],
      tags: json['tags'],
      difficultyLevel: json['difficultyLevel'],
      orderIndex: json['orderIndex'],
    );
  }
}

class BigTaskModel {
  final int bigTaskId;
  final String title;
  final String icon;
  final int orderIndex;
  final String startTime;
  final String endTime;
  final int? presetId;
  final List<SmallTaskModel> smallTasks;

  BigTaskModel({
    required this.bigTaskId,
    required this.title,
    required this.icon,
    required this.orderIndex,
    required this.startTime,
    required this.endTime,
    this.presetId,
    required this.smallTasks,
  });

  factory BigTaskModel.fromJson(Map<String, dynamic> json) {
    return BigTaskModel(
      bigTaskId: json['bigTaskId'],
      title: json['title'],
      icon: json['icon'],
      orderIndex: json['orderIndex'],
      startTime: json['startTime'],
      endTime: json['endTime'],
      presetId: json['presetId'],
      smallTasks: (json['smallTasks'] as List)
          .map((e) => SmallTaskModel.fromJson(e))
          .toList(),
    );
  }
}
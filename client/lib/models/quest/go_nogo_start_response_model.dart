// go-nogo 게임 시작 (response)

class GoNoGoStartResponseModel {
  final String sessionId;
  final List<GoNoGoStimulus> stimuli;
  final int timeLimit;
  final int totalCount;

  GoNoGoStartResponseModel({
    required this.sessionId,
    required this.stimuli,
    required this.timeLimit,
    required this.totalCount,
  });

  factory GoNoGoStartResponseModel.fromJson(Map<String, dynamic> json) {
    var data = json['data'];
    return GoNoGoStartResponseModel(
      sessionId: data['sessionId'],
      stimuli: (data['stimuli'] as List).map((i) => GoNoGoStimulus.fromJson(i)).toList(),
      timeLimit: data['timeLimit'],
      totalCount: data['totalCount'],
    );
  }
}

class GoNoGoStimulus {
  final int index;
  final String type; // "GO" 또는 "NOGO"
  final String image; // "monster_green", "bomb_red" 등

  GoNoGoStimulus({required this.index, required this.type, required this.image});

  factory GoNoGoStimulus.fromJson(Map<String, dynamic> json) {
    return GoNoGoStimulus(
      index: json['index'],
      type: json['type'],
      image: json['image'],
    );
  }
}
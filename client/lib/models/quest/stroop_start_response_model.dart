// stroop 게임 시작 (response)

class StroopStartResponseModel {
  final String sessionId;
  final List<StroopStimulus> stimuli;
  final int timeLimit;
  final int totalCount;

  StroopStartResponseModel({
    required this.sessionId,
    required this.stimuli,
    required this.timeLimit,
    required this.totalCount,
  });

  factory StroopStartResponseModel.fromJson(Map<String, dynamic> json) {
    final data = json['data'];
    var list = data['stimuli'] as List;
    List<StroopStimulus> stimuliList = list.map((i) => StroopStimulus.fromJson(i)).toList();

    return StroopStartResponseModel(
      sessionId: data['sessionId'],
      stimuli: stimuliList,
      timeLimit: data['timeLimit'],
      totalCount: data['totalCount'],
    );
  }
}

class StroopStimulus {
  final int index;
  final String word;
  final String inkColor;
  final bool isMatch;

  StroopStimulus({
    required this.index,
    required this.word,
    required this.inkColor,
    required this.isMatch,
  });

  factory StroopStimulus.fromJson(Map<String, dynamic> json) {
    return StroopStimulus(
      index: json['index'],
      word: json['word'],
      inkColor: json['inkColor'],
      isMatch: json['isMatch'],
    );
  }
}
// stroop 정답 제출 (request)

class StroopSubmitRequestModel {
  final int childId;
  final String sessionId;
  final List<StroopAnswerModel> answers;

  StroopSubmitRequestModel({
    required this.childId,
    required this.sessionId,
    required this.answers,
  });

  Map<String, dynamic> toJson() => {
    'childId': childId,
    'sessionId': sessionId,
    'answers': answers.map((a) => a.toJson()).toList(),
  };
}

class StroopAnswerModel {
  final int index;
  final String selectedColor;
  final int responseTime;

  StroopAnswerModel({
    required this.index,
    required this.selectedColor,
    required this.responseTime,
  });

  Map<String, dynamic> toJson() => {
    'index': index,
    'selectedColor': selectedColor,
    'responseTime': responseTime,
  };
}
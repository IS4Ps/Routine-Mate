// go-nogo 정답 제출


class GoNoGoSubmitRequestModel {
  final int childId;
  final String sessionId;
  final List<GoNoGoAnswerModel> answers;

  GoNoGoSubmitRequestModel({required this.childId, required this.sessionId, required this.answers});

  Map<String, dynamic> toJson() => {
    "childId": childId,
    "sessionId": sessionId,
    "answers": answers.map((a) => a.toJson()).toList(),
  };
}

class GoNoGoAnswerModel {
  final int index;
  final bool tapped;
  final int responseTime;

  GoNoGoAnswerModel({required this.index, required this.tapped, required this.responseTime});

  Map<String, dynamic> toJson() => {
    "index": index,
    "tapped": tapped,
    "responseTime": responseTime,
  };
}
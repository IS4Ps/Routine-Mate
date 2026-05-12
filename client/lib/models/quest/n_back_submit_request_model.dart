// n-back 정답 제출


class NBackSubmitRequestModel {
  final int childId;
  final String sessionId;
  final List<NBackAnswerModel> answers;

  NBackSubmitRequestModel({
    required this.childId,
    required this.sessionId,
    required this.answers,
  });

  Map<String, dynamic> toJson() => {
    "childId": childId,
    "sessionId": sessionId,
    "answers": answers.map((e) => e.toJson()).toList(),
  };
}

class NBackAnswerModel {
  final int index;
  final bool matched;

  NBackAnswerModel({required this.index, required this.matched});

  Map<String, dynamic> toJson() => {
    "index": index,
    "matched": matched,
  };
}
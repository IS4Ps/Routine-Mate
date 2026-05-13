// 감정 기록 저장

class FeelingRequestModel {
  final int childId;
  final String date; // "YYYY-MM-DD" 형식
  final String primaryEmotion;
  final String secondaryEmotion;
  final int score;

  FeelingRequestModel({
    required this.childId,
    required this.date,
    required this.primaryEmotion,
    this.secondaryEmotion = "NONE",
    this.score = 5,
  });

  Map<String, dynamic> toJson() {
    return {
      "childId": childId,
      "date": date,
      "primaryEmotion": primaryEmotion,
      "secondaryEmotion": secondaryEmotion,
      "score": score,
    };
  }
}
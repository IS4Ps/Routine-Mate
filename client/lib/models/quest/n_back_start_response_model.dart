// n-back 게임 시작 (response)
class NBackStartResponseModel {
  final String sessionId;
  final int nLevel;
  final List<StimulusModel> stimuli;
  final int displayTime;
  final int intervalTime;
  final int totalCount;

  NBackStartResponseModel({
    required this.sessionId,
    required this.nLevel,
    required this.stimuli,
    required this.displayTime,
    required this.intervalTime,
    required this.totalCount,
  });

  factory NBackStartResponseModel.fromJson(Map<String, dynamic> json) {
    // 1. 'data' 필드가 없는 경우를 대비한 안전장치
    final data = json['data'];
    if (data == null) {
      throw Exception("응답 데이터에 'data' 필드가 없습니다.");
    }

    return NBackStartResponseModel(
      // 2. 각 필드 뒤에 .toInt()를 붙여 데이터 타입 에러를 방지합니다.
      sessionId: data['sessionId']?.toString() ?? "",
      nLevel: (data['nLevel'] ?? 0).toInt(),
      stimuli: (data['stimuli'] as List? ?? [])
          .map((item) => StimulusModel.fromJson(item))
          .toList(),
      displayTime: (data['displayTime'] ?? 2000).toInt(), // 기본값 2초
      intervalTime: (data['intervalTime'] ?? 1000).toInt(), // 기본값 1초
      totalCount: (data['totalCount'] ?? 0).toInt(),
    );
  }
}

class StimulusModel {
  final int index;
  final String value;

  StimulusModel({required this.index, required this.value});

  factory StimulusModel.fromJson(Map<String, dynamic> json) {
    return StimulusModel(
      index: (json['index'] ?? 0).toInt(),
      value: json['value']?.toString() ?? "",
    );
  }
}
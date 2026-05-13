// 월간 감정 캘린더 조회


class MonthlyMoodModel {
  final int year;
  final int month;
  final int totalCount;
  final double avgScore;
  // 날짜(YYYY-MM-DD)를 Key로 사용하는 Map 구조
  final Map<String, MoodLogDetail> moodMap;

  MonthlyMoodModel({
    required this.year,
    required this.month,
    required this.totalCount,
    required this.avgScore,
    required this.moodMap,
  });

  factory MonthlyMoodModel.fromJson(Map<String, dynamic> json) {
    final data = json['data'];
    final Map<String, dynamic> rawMoodMap = data['moodMap'] ?? {};

    // JSON의 Map 데이터를 MoodLogDetail 객체 Map으로 변환
    final Map<String, MoodLogDetail> transformedMap = rawMoodMap.map(
          (key, value) => MapEntry(key, MoodLogDetail.fromJson(value)),
    );

    return MonthlyMoodModel(
      year: data['year'],
      month: data['month'],
      totalCount: data['totalCount'],
      avgScore: (data['avgScore'] as num).toDouble(),
      moodMap: transformedMap,
    );
  }
}

class MoodLogDetail {
  final int moodId;
  final String date;
  final String primaryEmotion; // 아이가 선택한 5가지 메인 감정
  final String secondaryEmotion; // 보조 감정
  final int score;

  MoodLogDetail({
    required this.moodId,
    required this.date,
    required this.primaryEmotion,
    required this.secondaryEmotion,
    required this.score,
  });

  factory MoodLogDetail.fromJson(Map<String, dynamic> json) {
    return MoodLogDetail(
      moodId: json['moodId'],
      date: json['date'],
      primaryEmotion: json['primaryEmotion'],
      secondaryEmotion: json['secondaryEmotion'],
      score: json['score'],
    );
  }

  // 아이 쪽에서 정의된 5가지 메인 감정 텍스트를 이모지로 매핑
  String get emotionEmoji {
    switch (primaryEmotion) {
    // 😆 매우 좋음 카테고리 (detailMoodMap 0번)
      case '매우 좋음':
      case '신나요':
      case '최고예요':
      case '활기차요':
      case '짜릿해요':
      case '즐거워요':
      case '환상적예요':
        return '😆';

    // 😊 좋음 카테고리 (detailMoodMap 1번)
      case '좋음':
      case '행복해요':
      case '편안해요':
      case '감사해요':
      case '뿌듯해요':
      case '화사해요':
      case '쾌적해요':
        return '😊';

    // 😐 보통 카테고리 (detailMoodMap 2번)
      case '보통':
      case '그저그래요':
      case '차분해요':
      case '평온해요':
      case '멍해요':
      case '심심해요':
      case '졸려요':
        return '😐';

    // 😟 나쁨 카테고리 (detailMoodMap 3번)
      case '나쁨':
      case '속상해요':
      case '우울해요':
      case '지쳐요':
      case '불안해요':
      case '답답해요':
      case '걱정돼요':
        return '😟';

    // 😭 매우 나쁨 카테고리 (detailMoodMap 4번)
      case '매우 나쁨':
      case '화나요':
      case '짜증나요':
      case '슬퍼요':
      case '서운해요':
      case '억울해요':
      case '힘들어요':
        return '😭';

      default:
      // 매칭되는 게 없을 때 로그를 확인하기 위해 텍스트를 직접 반환하거나 비워둡니다.
        return '';
    }
  }
}
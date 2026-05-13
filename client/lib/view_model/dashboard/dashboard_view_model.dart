import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:frontend/models/dashboard/monthly_mood_model.dart';
import '../../repository/dashboard/dashboard_repository.dart';

class DashboardViewModel extends ChangeNotifier {
  final DashboardRepository _repository = DashboardRepository();

  bool _isLoading = false;
  MonthlyMoodModel? _monthlyMoodData;
  String _errorMessage = "";

  // Getter들
  bool get isLoading => _isLoading;
  MonthlyMoodModel? get monthlyMoodData => _monthlyMoodData;
  String get errorMessage => _errorMessage;

  Map<String, dynamic>? _weeklyStats;
  Map<String, dynamic>? get weeklyStats => _weeklyStats;
  double get avgCompletionRate => (_weeklyStats?['avgCompletionRate'] ?? 0.0).toDouble();

  // 현재 테스트 중인 토큰 (dotenv 활용)
  final String _testToken = dotenv.env['TEST_TOKEN'] ?? "";

  // 토큰의 Payload에서 'sub' 값을 추출하는 함수 (기존 QuestViewModel 로직 유지)
  int _getChildIdFromToken(String token) {
    try {
      final parts = token.split('.');
      if (parts.length != 3) return 1;

      String payload = parts[1];
      while (payload.length % 4 != 0) {
        payload += '=';
      }

      final String decoded = utf8.decode(base64Url.decode(payload));
      final Map<String, dynamic> json = jsonDecode(decoded);

      return int.parse(json['sub'].toString());
    } catch (e) {
      print("[DashboardViewModel] 토큰 디코딩 에러: $e");
      return 1; // 실패 시 기본값
    }
  }

  // 월간 감정 캘린더 조회
  Future<void> fetchMonthlyMood({int? year, int? month}) async {
    _isLoading = true;
    _errorMessage = "";
    notifyListeners();

    try {
      final int dynamicChildId = _getChildIdFromToken(_testToken);

      // 파라미터가 없으면 현재 연도와 월을 사용
      final now = DateTime.now();
      final int targetYear = year ?? now.year;
      final int targetMonth = month ?? now.month;

      print("[DashboardViewModel] 월간 감정 로드 시작 (year: $targetYear, month: $targetMonth)");

      final MonthlyMoodModel? data = await _repository.getMonthlyMood(
          dynamicChildId,
          _testToken,
          targetYear,
          targetMonth
      );

      if (data != null) {
        _monthlyMoodData = data;

        print("[Dashboard API 성공] 상태 코드: 200 ");
        print("[DashboardViewModel] 데이터 로드 성공: ${data.totalCount}개의 기록 발견");
      } else {
        _errorMessage = "데이터를 불러오지 못했습니다.";
      }
    } catch (e) {
      print("[DashboardViewModel 에러] $e");
      _errorMessage = "서버 연결 중 오류가 발생했습니다.";
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> fetchWeeklyStats() async {
    final int childId = _getChildIdFromToken(_testToken);
    print("[ViewModel] 주간 통계 로드 시작 (childId: $childId)");

    final data = await _repository.getWeeklyStats(childId);
    if (data != null) {
      _weeklyStats = data;
      print("[ViewModel] 주간 통계 로드 완료: ${data['avgCompletionRate']}%");
      notifyListeners();
    }
  }
}
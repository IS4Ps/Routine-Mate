import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:frontend/models/quest/feeling_model.dart';
import 'package:frontend/models/quest/today_mission_model.dart';
import 'package:frontend/models/quest/weekly_stats_model.dart'; // 주간 통계 모델 추가 필요
import '../../repository/quest/quest_repository.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:frontend/models/quest/equipped_item_model.dart';
import 'package:frontend/models/quest/n_back_start_request_model.dart';
import 'package:frontend/models/quest/n_back_start_response_model.dart';
import 'package:frontend/models/quest/n_back_submit_response_model.dart';
import 'package:frontend/models/quest/n_back_submit_request_model.dart';
import 'package:frontend/models/quest/go_nogo_start_request_model.dart';
import 'package:frontend/models/quest/go_nogo_start_response_model.dart';
import 'package:frontend/models/quest/go_nogo_submit_request_model.dart';
import 'package:frontend/models/quest/go_nogo_submit_response_model.dart';
import 'package:frontend/models/quest/stroop_start_request_model.dart';
import 'package:frontend/models/quest/stroop_start_response_model.dart';
import 'package:frontend/models/quest/stroop_submit_request_model.dart';
import 'package:frontend/models/quest/stroop_submit_response_model.dart';


class QuestViewModel extends ChangeNotifier {
  final QuestRepository _repository = QuestRepository();

  int _currentStep = 0;
  bool _isLoading = false;
  String _message = "오늘의 기분은 어때?";
  FeelingModel? _feelingData;
  List<TodayMissionModel> _todayMissions = [];
  WeeklyStatsModel? _weeklyStats;

  int get currentStep => _currentStep;
  bool get isLoading => _isLoading;
  String get message => _message;
  FeelingModel? get feelingData => _feelingData;
  List<TodayMissionModel> get todayMissions => _todayMissions;

  WeeklyStatsModel? get weeklyStats => _weeklyStats;

  void changeStep(int step) {
    _currentStep = step;
    notifyListeners();
  }

  // 현재 테스트 중인 토큰
  final String _testToken = dotenv.env['TEST_TOKEN'] ?? "";

  // 토큰의 Payload에서 'sub' 값을 추출하는 함수
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
      print("토큰 디코딩 에러: $e");
      return 1;
    }
  }

  // 오늘 감정 조회
  Future<void> loadTodayMood() async {
    _isLoading = true;
    _message = "가져오는 중...";
    notifyListeners();

    final int dynamicChildId = _getChildIdFromToken(_testToken);
    _feelingData = await _repository.getTodayMood(dynamicChildId, _testToken);

    if (_feelingData != null) {
      if (_feelingData!.primaryEmotion == "NONE" ||
          _feelingData!.primaryEmotion == "기록 없음") {
        _message = "오늘의 기분은 어때?";
      } else {
        _message = "오늘은 ${_feelingData!.primaryEmotion}!";
      }
    } else {
      _message = "서버 연결에 실패했어요.";
    }

    _isLoading = false;
    notifyListeners();
  }

  // 감정 기록 저장 조회
  Future<void> saveFeeling(String selectedEmotion) async {
    _isLoading = true;
    notifyListeners();

    final int dynamicChildId = _getChildIdFromToken(_testToken);
    final bool isSuccess = await _repository.registerTodayMood(
        _testToken, dynamicChildId, selectedEmotion);

    if (isSuccess) {
      await loadTodayMood();
    } else {
      _message = "기분 저장에 실패했어요.";
      _isLoading = false;
      notifyListeners();
    }
  }

  // 오늘의 미션 목록 조회
  Future<void> fetchTodayMissions() async {
    _isLoading = true;
    notifyListeners();

    try {
      final int dynamicChildId = _getChildIdFromToken(_testToken);
      print("[ViewModel] 미션 목록 로드 시작 (childId: $dynamicChildId)");

      final List<TodayMissionModel> missions =
      await _repository.getTodayMissions(dynamicChildId, _testToken);

      _todayMissions = missions;
    } catch (e) {
      print("[ViewModel 에러] 미션 로드 실패: $e");
      _todayMissions = [];
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // 주간 달성률 조회 (퀘스트)
  Future<void> fetchWeeklyStats() async {
    _isLoading = true;
    notifyListeners();

    try {
      final int dynamicChildId = _getChildIdFromToken(_testToken);
      print("[ViewModel] 주간 통계 로드 시작 (childId: $dynamicChildId)");

      // Repository에 getWeeklyStats 메서드를 구현해야 함
      final WeeklyStatsModel? stats = await _repository.getWeeklyStats(dynamicChildId, _testToken);

      if (stats != null) {
        _weeklyStats = stats;
        print("[ViewModel] 주간 통계 로드 완료: ${stats.weeklySuccessRate}%");
      }
    } catch (e) {
      print("[ViewModel 에러] 주간 통계 로드 실패: $e");
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // 현재 장착 아이템 조회
  List<EquippedItemModel> _equippedItems = [];
  List<EquippedItemModel> get equippedItems => _equippedItems;

  Future<void> fetchEquippedItems() async {
    _isLoading = true;
    notifyListeners();

    try {
      final int dynamicChildId = _getChildIdFromToken(_testToken);
      final List<EquippedItemModel> items = await _repository.getEquippedItems(dynamicChildId, _testToken);

      // 장착된 아이템만 필터링해서 저장 (isEquipped가 true인 것만)
      _equippedItems = items.where((item) => item.isEquipped).toList();

      print("[ViewModel] 장착 아이템 로드 완료: ${_equippedItems.length}개");
    } catch (e) {
      print("[ViewModel 에러] 장착 아이템 로드 실패: $e");
      _equippedItems = [];
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // --- N-Back 게임 관련 상태 ---
  NBackStartResponseModel? _nBackData;
  NBackStartResponseModel? get nBackData => _nBackData;

  Future<void> startNBackGame(int nLevel) async {
    _isLoading = true;
    _message = "게임 데이터를 불러오는 중...";
    notifyListeners();

    try {
      final int dynamicChildId = _getChildIdFromToken(_testToken);

      final request = NBackStartRequestModel(
        childId: dynamicChildId,
        nLevel: nLevel,
        totalCount: 20,
      );

      // Repository로부터 데이터를 받아와 직접 할당
      final response = await _repository.startNBackGame(_testToken, request);

      if (response != null) {
        _nBackData = response;
        _message = "게임 시작!";
        print("[ViewModel] N-Back 로드 완료: ${response.sessionId}");
      } else {
        _nBackData = null;
        _message = "게임 데이터를 가져오지 못했습니다.";
      }
    } catch (e) {
      print("[ViewModel 에러] N-Back 시작 실패: $e");
      _nBackData = null;
      _message = "서버 연결에 실패했어요.";
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // N-Back 게임 결과
  NBackSubmitResponseModel? _nBackResult;
  NBackSubmitResponseModel? get nBackResult => _nBackResult;

  Future<bool> submitNBackGame(List<NBackAnswerModel> userAnswers) async {
    // 시작 API를 통해 받은 데이터가 없으면 진행 불가
    if (_nBackData == null) {
      _message = "게임 세션이 유효하지 않습니다.";
      notifyListeners();
      return false;
    }

    _isLoading = true;
    _message = "결과를 저장하는 중...";
    notifyListeners();

    try {
      final int dynamicChildId = _getChildIdFromToken(_testToken);

      // 명세서에 따른 요청 객체 생성
      final request = NBackSubmitRequestModel(
        childId: dynamicChildId,
        sessionId: _nBackData!.sessionId, // 시작 시 받은 sessionId 필수 포함
        answers: userAnswers,
      );

      // Repository를 통해 API 호출 (NBackSubmitResponseModel 반환 가정)
      final response = await _repository.submitNBackGame(_testToken, request);

      if (response != null) {
        _nBackResult = response;
        _message = "게임 완료! 보상을 획득했습니다.";
        print("[ViewModel] N-Back 결과 제출 성공: ${response.score}점");
        return true;
      } else {
        _message = "결과 저장에 실패했습니다.";
        return false;
      }
    } catch (e) {
      print("[ViewModel 에러] N-Back 제출 실패: $e");
      _message = "서버 연결에 실패했어요.";
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Go/No-Go 게임 시작
  GoNoGoStartResponseModel? _goNoGoData;
  GoNoGoStartResponseModel? get goNoGoData => _goNoGoData;

  Future<void> startGoNoGoGame({int difficulty = 1, int totalCount = 20}) async {
    _isLoading = true;
    _message = "게임 데이터를 불러오는 중...";
    notifyListeners();

    try {
      final int dynamicChildId = _getChildIdFromToken(_testToken);

      final request = GoNoGoStartRequestModel(
        childId: dynamicChildId,
        totalCount: totalCount,
        difficulty: difficulty,
      );

      // Repository를 통해 API 호출
      final response = await _repository.startGoNoGoGame(_testToken, request);

      if (response != null) {
        _goNoGoData = response;
        _message = "게임 시작!";
        print("[ViewModel] Go/No-Go 로드 완료: ${response.sessionId}");
      } else {
        _goNoGoData = null;
        _message = "게임 데이터를 가져오지 못했습니다.";
      }
    } catch (e) {
      print("[ViewModel 에러] Go/No-Go 시작 실패: $e");
      _goNoGoData = null;
      _message = "서버 연결에 실패했어요.";
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Go/No-Go 정답 제출
  GoNoGoSubmitResponseModel? _goNoGoResult;
  GoNoGoSubmitResponseModel? get goNoGoResult => _goNoGoResult;

  Future<bool> submitGoNoGoGame(List<GoNoGoAnswerModel> userAnswers) async {
    if (_goNoGoData == null) {
      _message = "게임 세션이 유효하지 않습니다.";
      notifyListeners();
      return false;
    }

    _isLoading = true;
    _message = "결과를 저장하는 중...";
    notifyListeners();

    try {
      final int dynamicChildId = _getChildIdFromToken(_testToken);

      final request = GoNoGoSubmitRequestModel(
        childId: dynamicChildId,
        sessionId: _goNoGoData!.sessionId,
        answers: userAnswers,
      );

      final response = await _repository.submitGoNoGoGame(_testToken, request);

      if (response != null) {
        _goNoGoResult = response;
        _message = "게임 완료! 보상을 획득했습니다.";
        print("[ViewModel] Go/No-Go 제출 성공: ${response.score}점 획득");
        return true;
      } else {
        _message = "결과 저장에 실패했습니다.";
        return false;
      }
    } catch (e) {
      print("[ViewModel 에러] Go/No-Go 제출 실패: $e");
      _message = "서버 연결에 실패했어요.";
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // stroop 게임 시작
  StroopStartResponseModel? _stroopData;
  StroopStartResponseModel? get stroopData => _stroopData;

  Future<void> startStroopGame({int totalCount = 20, int difficulty = 1}) async {
    _isLoading = true;
    _message = "스트룹 게임 데이터를 불러오는 중...";
    notifyListeners();

    try {
      final int dynamicChildId = _getChildIdFromToken(_testToken);

      // 모델 생성
      final request = StroopStartRequestModel(
        childId: dynamicChildId,
        totalCount: totalCount,
        difficulty: difficulty,
      );

      // Repository 호출
      final response = await _repository.startStroopGame(_testToken, request);

      if (response != null) {
        _stroopData = response;
        _message = "스트룹 게임 시작!";
        print("[ViewModel] Stroop 로드 완료: ${response.sessionId}");
      } else {
        _stroopData = null;
        _message = "게임 데이터를 가져오지 못했습니다.";
      }
    } catch (e) {
      print("[ViewModel 에러] Stroop 시작 실패: $e");
      _stroopData = null;
      _message = "서버 연결에 실패했어요.";
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // stroop 정답 제출
  StroopSubmitResponseModel? _stroopResult;
  StroopSubmitResponseModel? get stroopResult => _stroopResult;

  /// [추가] 스트룹 게임 결과 제출 메서드
  Future<bool> submitStroopGame(List<StroopAnswerModel> userAnswers) async {
    // 시작 API를 통해 받은 데이터(sessionId)가 없으면 진행 불가
    if (_stroopData == null) {
      _message = "게임 세션이 유효하지 않습니다.";
      notifyListeners();
      return false;
    }

    _isLoading = true;
    _message = "결과를 저장하는 중...";
    notifyListeners();

    try {
      final int dynamicChildId = _getChildIdFromToken(_testToken);

      // image_b01933.png 명세에 따른 요청 객체 생성
      final request = StroopSubmitRequestModel(
        childId: dynamicChildId,
        sessionId: _stroopData!.sessionId, // 시작 시 받은 sessionId 사용
        answers: userAnswers,
      );

      // Repository를 통해 API 호출
      final response = await _repository.submitStroopGame(_testToken, request);

      if (response != null) {
        _stroopResult = response;
        _message = "게임 완료! 보상을 획득했습니다.";
        print("[ViewModel] Stroop 결과 제출 성공: ${response.score}점");
        return true;
      } else {
        _message = "결과 저장에 실패했습니다.";
        return false;
      }
    } catch (e) {
      print("[ViewModel 에러] Stroop 제출 실패: $e");
      _message = "서버 연결에 실패했어요.";
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
}
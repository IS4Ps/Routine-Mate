import 'dart:convert';
import 'package:frontend/models/quest/feeling_model.dart';
import 'package:frontend/models/quest/feeling_request_model.dart';
import 'package:frontend/models/quest/today_mission_model.dart'; // 모델 임포트 확인
import '../../services/quest/quest_api_service.dart';
import 'package:frontend/models/quest/weekly_stats_model.dart';
import 'package:frontend/models/quest/equipped_item_model.dart';
import 'package:frontend/models/quest/n_back_start_request_model.dart';
import 'package:frontend/models/quest/n_back_start_response_model.dart';
import 'package:frontend/models/quest/n_back_submit_request_model.dart';
import 'package:frontend/models/quest/n_back_submit_response_model.dart';
import 'package:frontend/models/quest/go_nogo_start_request_model.dart';
import 'package:frontend/models/quest/go_nogo_start_response_model.dart';
import 'package:frontend/models/quest/go_nogo_submit_request_model.dart';
import 'package:frontend/models/quest/go_nogo_submit_response_model.dart';
import 'package:frontend/models/quest/stroop_start_request_model.dart';
import 'package:frontend/models/quest/stroop_start_response_model.dart';
import 'package:frontend/models/quest/stroop_submit_request_model.dart';
import 'package:frontend/models/quest/stroop_submit_response_model.dart';


class QuestRepository {
  final QuestApiService _apiService = QuestApiService();

  // 오늘 감정 조회
  Future<FeelingModel?> getTodayMood(int childId, String token) async {
    try {
      print("[API 호출] 오늘의 기분 조회 시작 (childId: $childId)");

      final response = await _apiService.fetchTodayMood(childId, token);

      if (response.statusCode == 200) {
        print("[API 성공] 상태 코드: ${response.statusCode}");
        print("[응답 데이터] ${response.body}");

        final body = jsonDecode(utf8.decode(response.bodyBytes));

        if (body['data'] != null) {
          return FeelingModel.fromJson(body['data']);
        } else {
          print("[데이터 분석] 성공했지만 'data'가 비어있어 'NONE'을 반환합니다.");
          return FeelingModel(primaryEmotion: "NONE", score: 0);
        }
      } else {
        print("[API 실패] 상태 코드: ${response.statusCode}");
        print("[에러 내용] ${response.body}");
        return null;
      }
    } catch (e) {
      print("[Repository 에러] 네트워크/런타임 문제 발생: $e");
      return null;
    }
  }

  // 감정 기록 저장
  Future<bool> registerTodayMood(String token, int childId, String emotion) async {
    try {
      // 오늘 날짜 생성
      String todayDate = DateTime.now().toString().split(' ')[0];

      final request = FeelingRequestModel(
        childId: childId,
        date: todayDate,
        primaryEmotion: emotion,
      );

      final response = await _apiService.postTodayMood(token, request);

      if (response.statusCode == 200 || response.statusCode == 201) {
        print("[API 성공] 기분 등록 완료");
        return true;
      } else {
        print("[API 실패] 코드: ${response.statusCode}, 내용: ${response.body}");
        return false;
      }
    } catch (e) {
      print("[Repository 에러]: $e");
      return false;
    }
  }

  // 오늘 미션 목록 조회
  Future<List<TodayMissionModel>> getTodayMissions(int childId, String token) async {
    try {
      print("[API 호출] 오늘의 미션 목록 조회 시작 (childId: $childId)");

      final response = await _apiService.fetchTodayMissions(childId, token);

      if (response.statusCode == 200) {
        print("[API 성공] 상태 코드: ${response.statusCode}");

        // 응답 본문 디코딩 (한글 깨짐 방지)
        final body = jsonDecode(utf8.decode(response.bodyBytes));

        if (body['data'] != null && body['data'] is List) {
          final List<dynamic> dataList = body['data'];

          // JSON 리스트를 TodayMissionModel 리스트로 변환
          return dataList.map((json) => TodayMissionModel.fromJson(json)).toList();
        } else {
          print("[데이터 분석] 'data' 필드가 비어있거나 리스트 형식이 아닙니다.");
          return [];
        }
      } else {
        print("[API 실패] 상태 코드: ${response.statusCode}, 내용: ${response.body}");
        return [];
      }
    } catch (e) {
      print("[Repository 에러] 미션 목록 로드 중 문제 발생: $e");
      return [];
    }
  }

  // 주간 달성률 조회 (퀘스트)
  Future<WeeklyStatsModel?> getWeeklyStats(int childId, String token) async {
    try {
      // Dio 대신 프로젝트에서 사용하는 apiService의 http 호출 사용
      final response = await _apiService.fetchWeeklyStats(childId, token);

      if (response.statusCode == 200) {
        // http 패키지는 response.body를 직접 decode해야 합니다.
        final Map<String, dynamic> decodedData = jsonDecode(utf8.decode(response.bodyBytes));
        return WeeklyStatsModel.fromJson(decodedData['data']);
      }
      return null;
    } catch (e) {
      print("통계 API 레포지토리 에러: $e");
      return null;
    }
  }

  // 현재 장착 아이템 조회 추가
  Future<List<EquippedItemModel>> getEquippedItems(int childId, String token) async {
    try {
      print("[API 호출] 현재 장착 아이템 조회 시작 (childId: $childId)");

      final response = await _apiService.fetchEquippedItems(childId, token);

      if (response.statusCode == 200) {
        print("[API 성공] 장착 아이템 로드 완료");

        // 한글 깨짐 방지 디코딩
        final body = jsonDecode(utf8.decode(response.bodyBytes));

        if (body['data'] != null && body['data'] is List) {
          final List<dynamic> dataList = body['data'];

          // JSON 리스트를 EquippedItemModel 리스트로 변환
          return dataList
              .map((json) => EquippedItemModel.fromJson(json))
              .toList();
        } else {
          print("[데이터 분석] 장착된 아이템 데이터가 없습니다.");
          return [];
        }
      } else {
        print("[API 실패] 상태 코드: ${response.statusCode}, 내용: ${response.body}");
        return [];
      }
    } catch (e) {
      print("[Repository 에러] 장착 아이템 로드 중 문제 발생: $e");
      return [];
    }
  }

  // n-back 게임 시작

  Future<NBackStartResponseModel?> startNBackGame(String token, NBackStartRequestModel request) async {
    try {
      final response = await _apiService.startNBackGame(token, request);

      // 1. 상태 코드 확인 로그 추가
      print("[Repository] 응답 코드: ${response.statusCode}");

      if (response.statusCode == 200 || response.statusCode == 201) {
        final String decodedBody = utf8.decode(response.bodyBytes);
        final Map<String, dynamic> jsonData = jsonDecode(decodedBody);

        // 2. 모델로 변환 시도 (여기서 null이 리턴되면 모델 클래스의 fromJson 문제)
        return NBackStartResponseModel.fromJson(jsonData);
      } else {
        // 3. 에러 발생 시 서버가 준 메시지 출력
        print("[Repository] 서버 에러 메시지: ${response.body}");
      }
    } catch (e, stacktrace) {
      // 4. 스택트레이스를 함께 출력하여 정확한 에러 위치 파악
      print("[Repository 에러] startNBackGame 상세: $e");
      print("[Repository 스택트레이스] $stacktrace");
    }
    return null;
  }

  // N-Back 게임 정답 제출 추가
  Future<NBackSubmitResponseModel?> submitNBackGame(
      String token, NBackSubmitRequestModel request) async {
    try {
      print("[Repository] N-Back 정답 제출 시작 (sessionId: ${request.sessionId})");

      final response = await _apiService.submitNBackGame(token, request);

      print("[Repository] 응답 코드: ${response.statusCode}");

      if (response.statusCode == 200 || response.statusCode == 201) {
        final String decodedBody = utf8.decode(response.bodyBytes);
        final Map<String, dynamic> jsonData = jsonDecode(decodedBody);

        // API 명세서의 Response Body 구조에 맞춰 모델 변환
        return NBackSubmitResponseModel.fromJson(jsonData);
      } else {
        print("[Repository] 제출 실패 서버 에러: ${response.body}");
        return null;
      }
    } catch (e, stacktrace) {
      print("[Repository 에러] submitNBackGame 상세: $e");
      print("[Repository 스택트레이스] $stacktrace");
      return null;
    }
  }

  // Go/No-Go 게임 시작
  Future<GoNoGoStartResponseModel?> startGoNoGoGame(
      String token, GoNoGoStartRequestModel request) async {
    try {
      print("[Repository] Go/No-Go 게임 시작 호출 (childId: ${request.childId}, difficulty: ${request.difficulty})");

      // API 서비스에서 POST 요청 호출 (URL: /api/v1/minigames/go-no-go/start)
      final response = await _apiService.startGoNoGoGame(token, request);

      print("[Repository] 응답 코드: ${response.statusCode}");

      if (response.statusCode == 200 || response.statusCode == 201) {
        final String decodedBody = utf8.decode(response.bodyBytes);
        final Map<String, dynamic> jsonData = jsonDecode(decodedBody);

        // API 응답 구조의 'data' 필드를 모델로 변환
        return GoNoGoStartResponseModel.fromJson(jsonData);
      } else {
        print("[Repository] Go/No-Go 시작 실패 서버 에러: ${response.body}");
        return null;
      }
    } catch (e, stacktrace) {
      print("[Repository 에러] startGoNoGoGame 상세 에러: $e");
      print("[Repository 스택트레이스] $stacktrace");
      return null;
    }
  }

  // Go/No-Go 정답 제출
  Future<GoNoGoSubmitResponseModel?> submitGoNoGoGame(
      String token, GoNoGoSubmitRequestModel request) async {
    try {
      print("[Repository] Go/No-Go 정답 제출 시작 (sessionId: ${request.sessionId})");

      final response = await _apiService.submitGoNoGoGame(token, request);

      print("[Repository] 응답 코드: ${response.statusCode}");

      if (response.statusCode == 200 || response.statusCode == 201) {
        final String decodedBody = utf8.decode(response.bodyBytes);
        final Map<String, dynamic> jsonData = jsonDecode(decodedBody);

        return GoNoGoSubmitResponseModel.fromJson(jsonData);
      } else {
        print("[Repository] 제출 실패 서버 에러: ${response.body}");
        return null;
      }
    } catch (e, stacktrace) {
      print("[Repository 에러] submitGoNoGoGame 상세: $e");
      print("[Repository 스택트레이스] $stacktrace");
      return null;
    }
  }

  // stroop 게임 시작
  Future<StroopStartResponseModel?> startStroopGame(
      String token, StroopStartRequestModel request) async {
    try {
      print("[Repository] Stroop 게임 시작 요청 (childId: ${request.childId})");

      // API 서비스를 통해 POST 요청 전송
      final response = await _apiService.startStroopGame(token, request);

      print("[Repository] 응답 코드: ${response.statusCode}");

      if (response.statusCode == 200 || response.statusCode == 201) {
        final String decodedBody = utf8.decode(response.bodyBytes);
        final Map<String, dynamic> jsonData = jsonDecode(decodedBody);

        print("[Repository] 데이터 연결 완료");
        return StroopStartResponseModel.fromJson(jsonData);
      } else {
        print("[Repository] Stroop 시작 실패: ${response.body}");
        return null;
      }
    } catch (e, stacktrace) {
      print("[Repository 에러] startStroopGame 상세: $e");
      print("[Repository 스택트레이스] $stacktrace");
      return null;
    }
  }

  // stroop 정답 제출
  Future<StroopSubmitResponseModel?> submitStroopGame(
      String token, StroopSubmitRequestModel request) async {
    try {
      print("[Repository] Stroop 정답 제출 시작 (sessionId: ${request.sessionId})");

      final response = await _apiService.submitStroopGame(token, request);

      print("[Repository] 응답 코드: ${response.statusCode}");

      if (response.statusCode == 200 || response.statusCode == 201) {
        // 한글 깨짐 방지 디코딩 적용
        final String decodedBody = utf8.decode(response.bodyBytes);
        final Map<String, dynamic> jsonData = jsonDecode(decodedBody);

        print("[Repository] Stroop 결과 분석 완료");
        return StroopSubmitResponseModel.fromJson(jsonData);
      } else {
        print("[Repository] Stroop 제출 실패 서버 에러: ${response.body}");
        return null;
      }
    } catch (e, stacktrace) {
      print("[Repository 에러] submitStroopGame 상세: $e");
      print("[Repository 스택트레이스] $stacktrace");
      return null;
    }
  }
}
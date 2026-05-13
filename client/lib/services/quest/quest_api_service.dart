import 'dart:convert'; // jsonEncode를 위해 추가
import 'package:http/http.dart' as http;
import 'package:frontend/models/quest/feeling_request_model.dart'; // import 확인!
import 'package:frontend/models/quest/n_back_start_request_model.dart';
import 'package:frontend/models/quest/n_back_submit_request_model.dart';
import 'package:frontend/models/quest/go_nogo_start_request_model.dart';
import 'package:frontend/models/quest/go_nogo_submit_request_model.dart';
import 'package:frontend/models/quest/stroop_start_request_model.dart';
import 'package:frontend/models/quest/stroop_submit_request_model.dart';

class QuestApiService {
  static const String baseUrl = "http://100.27.204.252:8080";

  // 오늘 감정 조회
  Future<http.Response> fetchTodayMood(int childId, String token) async {
    final url = Uri.parse('$baseUrl/api/v1/mood-logs/$childId/today');
    return await http.get(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
  }

  // 감정 기록 저장
  Future<http.Response> postTodayMood(String token, FeelingRequestModel request) async {
    final url = Uri.parse('$baseUrl/api/v1/mood-logs'); // Swagger 엔드포인트 확인
    return await http.post(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
      body: jsonEncode(request.toJson()), // 모델을 JSON 문자열로 변환
    );
  }

  // 오늘의 미션 목록 조회
  Future<http.Response> fetchTodayMissions(int childId, String token) async {
    final url = Uri.parse('$baseUrl/api/v1/missions/today/$childId');

    return await http.get(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
  }

  // 주간 달성률 조회 (퀘스트)
  Future<http.Response> fetchWeeklyStats(int childId, String token) async {
    final url = Uri.parse('$baseUrl/api/v1/missions/stats/$childId');

    return await http.get(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
  }

  // 현재 장착 아이템 조회 추가
  Future<http.Response> fetchEquippedItems(int childId, String token) async {
    // 백엔드 명세서의 URL: /api/v1/inventory/{childId}/equipped
    final url = Uri.parse('$baseUrl/api/v1/inventory/$childId/equipped');

    return await http.get(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );
  }

  // N-Back 게임 시작
  Future<http.Response> startNBackGame(String token, NBackStartRequestModel request) async {
    final url = Uri.parse('$baseUrl/api/v1/minigames/n-back/start');

    return await http.post(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
      body: jsonEncode(request.toJson()),
    );
  }

  // N-Back 정답 제출
  Future<http.Response> submitNBackGame(String token, NBackSubmitRequestModel request) async {
    final url = Uri.parse('$baseUrl/api/v1/minigames/n-back/submit');

    return await http.post(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
      body: jsonEncode(request.toJson()),
    );
  }

  // Go/No-Go 게임 시작
  Future<http.Response> startGoNoGoGame(String token, GoNoGoStartRequestModel request) async {
    final url = Uri.parse('$baseUrl/api/v1/minigames/go-no-go/start');

    return await http.post(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
      body: jsonEncode(request.toJson()),
    );
  }

  // Go/No-Go 정답 제출
  Future<http.Response> submitGoNoGoGame(String token, GoNoGoSubmitRequestModel request) async {
    final url = Uri.parse('$baseUrl/api/v1/minigames/go-no-go/submit');

    return await http.post(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
      body: jsonEncode(request.toJson()),
    );
  }

  // stroop 게임 시작
  Future<http.Response> startStroopGame(String token, StroopStartRequestModel request) async {
    final url = Uri.parse('$baseUrl/api/v1/minigames/stroop/start');

    return await http.post(
      url,
      headers: {
        'Authorization': 'Bearer $token', // 아이 토큰
        'Content-Type': 'application/json',
      },
      body: jsonEncode(request.toJson()), // childId, totalCount, difficulty 포함
    );
  }

  // stroop 정답 제출
  Future<http.Response> submitStroopGame(String token, StroopSubmitRequestModel request) async {
    final url = Uri.parse('$baseUrl/api/v1/minigames/stroop/submit');
    return await http.post(
      url,
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
      body: jsonEncode(request.toJson()),
    );
  }
}
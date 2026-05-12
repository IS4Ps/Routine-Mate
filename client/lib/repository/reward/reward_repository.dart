import 'dart:convert';
import 'package:frontend/models/reward/offline_reward_list_model.dart';
import '../../services/reward/reward_api_service.dart';
import 'package:frontend/models/reward/offline_reward_request_model.dart';

class RewardRepository {
  final RewardApiService _apiService = RewardApiService();

  // 오프라인 보상 목록 조회
  Future<List<OfflineRewardListModel>> getOfflineRewards(int childId, String token) async {
    try {
      print("[API 호출] 오프라인 보상 목록 조회 시작 (childId: $childId)");

      final response = await _apiService.fetchOfflineRewards(childId, token);

      if (response.statusCode == 200) {
        print("[API 성공] 상태 코드: ${response.statusCode}");

        // 한글 깨짐 방지 디코딩
        final body = jsonDecode(utf8.decode(response.bodyBytes));

        if (body['data'] != null && body['data'] is List) {
          final List<dynamic> dataList = body['data'];

          // JSON 리스트를 OfflineRewardListModel 리스트로 변환
          return dataList
              .map((json) => OfflineRewardListModel.fromJson(json))
              .toList();
        } else {
          print("[데이터 분석] 'data' 필드가 비어있거나 리스트 형식이 아닙니다.");
          return [];
        }
      } else {
        print("[API 실패] 상태 코드: ${response.statusCode}, 내용: ${response.body}");
        return [];
      }
    } catch (e) {
      print("[Repository 에러] 오프라인 보상 목록 로드 중 문제 발생: $e");
      return [];
    }
  }

  // 오프라인 보상 등록
  Future<bool> registerOfflineReward(String token, OfflineRewardRequestModel request) async {
    try {
      print("[API 호출] 오프라인 보상 등록 시작");

      final response = await _apiService.postOfflineReward(token, request);

      // 성공 상태 코드 확인 (일반적으로 200 또는 201)
      if (response.statusCode == 200 || response.statusCode == 201) {
        print("[API 성공] 오프라인 보상 등록 완료");
        return true;
      } else {
        print("[API 실패] 상태 코드: ${response.statusCode}, 내용: ${response.body}");
        return false;
      }
    } catch (e) {
      print("[Repository 에러] 오프라인 보상 등록 중 문제 발생: $e");
      return false;
    }
  }
}
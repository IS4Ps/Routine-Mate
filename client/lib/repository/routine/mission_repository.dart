import 'dart:convert';
import '../../services/routine/mission_api_service.dart';

class MissionRepository {
  final MissionApiService _apiService = MissionApiService();

  Future<bool> createMission(Map<String, dynamic> body) async {
    try {
      print("[API 호출] 미션 생성 시작");

      final response = await _apiService.createMission(body);

      if (response.statusCode == 200 || response.statusCode == 201) {
        print("[API 성공] 미션 생성 완료");
        print("[응답 데이터] ${response.body}");
        return true;
      } else {
        print("[API 실패] 상태 코드: ${response.statusCode}");
        print("[에러 내용] ${response.body}");
        return false;
      }
    } catch (e) {
      print("[Repository 에러] 네트워크/런타임 문제 발생: $e");
      return false;
    }
  }

  Future<List<dynamic>> getMissionsByDate(int childId, String date) async {
    try {
      print("[API 호출] 날짜별 미션 목록 조회 시작 (childId: $childId, date: $date)");

      final response = await _apiService.getMissionsByDate(childId, date);

      if (response.statusCode == 200) {
        print("[API 성공] 미션 목록 조회 완료");
        final data = jsonDecode(response.body);
        print("[목록 데이터] ${data['data']}");
        return data['data'] ?? [];
      } else {
        print("[API 실패] 상태 코드: ${response.statusCode}");
        return [];
      }
    } catch (e) {
      print("[Repository 에러] 네트워크/런타임 문제 발생: $e");
      return [];
    }
  }
}
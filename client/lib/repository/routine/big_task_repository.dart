import 'dart:convert';
import 'package:frontend/models/routine/big_task_model.dart';
import '../../services/routine/big_task_api_service.dart';

class BigTaskRepository {
  final BigTaskApiService _apiService = BigTaskApiService();

  Future<List<BigTaskModel>?> getBigTasks(int parentId, String token) async {
    try {
      print("[API 호출] 빅태스크 목록 조회 시작 (parentId: $parentId)");

      final response = await _apiService.fetchBigTasks(parentId, token);

      if (response.statusCode == 200) {
        print("[API 성공] 상태 코드: ${response.statusCode}");
        print("[응답 데이터] ${response.body}");

        final body = jsonDecode(response.body);
        return (body['data'] as List)
            .map((e) => BigTaskModel.fromJson(e))
            .toList();
      } else {
        print("[API 실패] 상태 코드: ${response.statusCode}");
        return null;
      }
    } catch (e) {
      print("[Repository 에러] 네트워크/런타임 문제 발생: $e");
      return null;
    }
  }

  Future<bool> createBigTask(Map<String, dynamic> body, String token) async {
    try {
      print("[API 호출] 빅태스크 생성 시작");

      final response = await _apiService.createBigTask(body, token);

      if (response.statusCode == 200 || response.statusCode == 201) {
        print("[API 성공] 빅태스크 생성 완료");
        return true;
      } else {
        print("[API 실패] 상태 코드: ${response.statusCode}");
        return false;
      }
    } catch (e) {
      print("[Repository 에러] 네트워크/런타임 문제 발생: $e");
      return false;
    }
  }
}